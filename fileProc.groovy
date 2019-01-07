// Groovy script for reading records from a database and calling an external REST API in a multi-threaded fashion

@GrabConfig(systemClassLoader=true)
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('ch.qos.logback:logback-classic:1.2.3')
@Grab('mysql:mysql-connector-java:8.0.13')
@Grab('org.apache.commons:commons-dbcp2:2.5.0')

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.time.TimeCategory

import java.util.concurrent.Executors
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Future
import java.util.Date

import dg.SecretKeyNotFoundException
import dg.RecordDAO
import dg.CryptoUtil
import dg.CardResponse
import dg.ProcessRequest

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

  def executor = Executors.newFixedThreadPool(config.client.maxThreads)
  def ecs = new ExecutorCompletionService(executor)
  RecordDAO recordDAO = new RecordDAO(config: config.db, recordsToLoad: config.client.recordsToLoad)
  def cryptoUtil = new CryptoUtil(secretKey: secretKey)

  while( true ) {
    def startTime = new Date()

    // get next records to process and reset statistics
    def recs = recordDAO.getAllWithStatusInitial()
    logger.info "Processing ${recs.size()} record(s)"
    def requestCnt = 0
    def successCnt = 0
    def failedCnt = 0
    def submitCnt = 0

    if( recs.size() > 0 ) {
      for(rec in recs) {
        line = cryptoUtil.decrypt(rec.rawData)
        ecs.submit( new ProcessRequest(recordDAO, config.client, rec.recordId, line) )
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

      def endTime = new Date()
      def duration = TimeCategory.minus(endTime, startTime)
      logger.info "Processed $requestCnt record(s) in ${duration} - $successCnt succeeded, $failedCnt failed"
    }

    logger.info "Going to sleep for 60s..."
    sleep(60000)
  }
}
catch( SecretKeyNotFoundException ex ) {
  logger.error("Encryption key not found in environment variable DG_SECRET_KEY")
}
catch( Exception ex ) {
  logger.error("Unknown error occured!?", ex)
}