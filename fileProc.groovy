// Sample Groovy script for reading a CSV file and calling an external REST API

@GrabConfig(systemClassLoader=true)
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('ch.qos.logback:logback-classic:1.2.3')
@Grab('mysql:mysql-connector-java:8.0.13')
@Grab('org.apache.commons:commons-dbcp2:2.5.0')

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.util.logging.Slf4j

import java.util.concurrent.Executors
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Future
import java.util.concurrent.Callable

import dg.SecretKeyNotFoundException
import dg.RecordDAO
import dg.CryptoUtil
import dg.CardResponse
import dg.LineParser
import dg.CardClient

// init logger
def logger = LoggerFactory.getLogger('fileProc')

def osName = System.properties['os.name']
def osVersion = System.properties['os.version']
logger.info "Groovy $GroovySystem.version File Processor running on $osName $osVersion"

// load application config
ConfigObject config = new ConfigSlurper().parse(new File('config.groovy').toURI().toURL())

try {
  def secretKey = System.getenv('DG_SECRET_KEY')
  if( secretKey == null )
    throw new SecretKeyNotFoundException()

  RecordDAO recordDAO = new RecordDAO(config: config.db)
  def cryptoUtil = new CryptoUtil(secretKey: secretKey)

  while( true ) {
    // get next records to process and reset statistics
    def recs = recordDAO.getAllWithStatusInitial()
    logger.info "Processing ${recs.size()} record(s)"
    def requestCnt = 0
    def successCnt = 0
    def failedCnt = 0
    def submitCnt = 0

    def executor = Executors.newFixedThreadPool(5)
    def ecs = new ExecutorCompletionService(executor)
    
    for(rec in recs) {
      line = cryptoUtil.decrypt(rec.rawData)
      ecs.submit( new ProcessRequest(recordDAO, config.config, rec.recordId, line) )
      submitCnt++
    }

    // loop until we have received the response for each submitted record
    Future future
    while( submitCnt > 0 ) {
      while( (future = ecs.poll()) != null ) {
        CardResponse result = future.get()
        logger.info result.toString()
        recordDAO.updateResponse(result)

        if( result.decision == 'SUCCESS' )
          successCnt++
        else if( result.decision == 'FAILED' )
          failedCnt++

        requestCnt++
        submitCnt--
      }
    }    

    logger.info "Processed $requestCnt record(s) in 0 - $successCnt succeeded, $failedCnt failed"
    sleep(60000)
  }
}
catch( SecretKeyNotFoundException ex ) {
  logger.error("Encryption key not found in environment variable DG_SECRET_KEY")
}
catch( Exception ex ) {
  logger.error("Unknown error occured!?", ex)
}

// class that will execute within a thread pool and calls a remote API
class ProcessRequest implements Callable {
  def logger = LoggerFactory.getLogger('ProcessRequest')

  def recordDAO
  def config
  def recordId
  def line

  def ProcessRequest( recordDAO, config, recordId, line ) {
    this.recordDAO = recordDAO
    this.config = config
    this.recordId = recordId
    this.line = line
  }

  def call() {
    def guid = UUID.randomUUID().toString()
    def client = new CardClient(
      cardUrl: config.url, 
      accountId: config.accountId, 
      apiUser: config.apiUser, 
      apiPass: config.apiPass)

    try {
      def parser = new LineParser()
      def lineReq = parser.parse(recordId, line)
      lineReq.guid = guid
      lineReq.ref = guid // we do this to make sure records work due to test data, not needed in PROD

      logger.info "Sending reference ${lineReq.ref} with amount ${lineReq.amount}"
      recordDAO.updateSent(lineReq)

      return client.purchase( lineReq )
    }
    catch( Exception ex ) {
      logger.warn "$guid with $recordId - Line processing failed: $ex"
      def resp = new CardResponse(recordId, 'ERROR', guid)
      resp.message = ex.getMessage()
      return resp
    }
  }
}