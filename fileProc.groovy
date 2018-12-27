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
/*import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.* */

import java.util.concurrent.Executors
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Future
import java.util.concurrent.Callable

import dg.SecretKeyNotFoundException
import dg.RecordDAO
import dg.CryptoUtil

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

  // TODO line parser
  // TODO unit tests

  RecordDAO recordDAO = new RecordDAO(config: config.db)
  def cryptoUtil = new CryptoUtil(secretKey: secretKey)

  while( true ) {
    // get next records to process and reset statistics
    def recs = recordDAO.getAllWithStatusInitial()
    logger.info "Processing ${recs.size()} record(s)"
    def requestCnt = 0
    def successCnt = 0
    def failedCnt = 0

    def executor = Executors.newFixedThreadPool(5)
    def ecs = new ExecutorCompletionService(executor)
    
    for(rec in recs) {
      logger.info "ds1>> $rec.rawData"
      line = cryptoUtil.decrypt(rec.rawData)
      ecs.submit( new ProcessRequest(recordId: rec.recordId, line: line) )

      Future future
      while( (future = ecs.poll()) != null ) {
        logger.info "f>> ${future.get()}"
        requestCnt++
      }
    }

    logger.info "Processed $requestCnt record(s) in 0 - $successCnt succeeded, $failedCnt failed"
    sleep(60000)
  }

  //executor.shutdown()
  logger.info "done!"
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

  def recordId
  def line

  def call() {
    try {
      return "here!>> $recordId $line"
    }
    catch( Exception ex ) {
      return "Exception"
    }    
  }
}