// Groovy script for reading a CSV file and storing data in a database

@GrabConfig(systemClassLoader=true)
@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('ch.qos.logback:logback-classic:1.2.3')

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.util.logging.Slf4j

import dg.SecretKeyNotFoundException

// init logger
def logger = LoggerFactory.getLogger('fileProc')

def osName = System.properties['os.name']
def osVersion = System.properties['os.version']
logger.info "Groovy $GroovySystem.version File Loader running on $osName $osVersion"

// load application config
ConfigObject config = new ConfigSlurper().parse(new File('config.groovy').toURI().toURL())

try {
  def secretKey = System.getenv('DG_SECRET_KEY')
  if( secretKey == null )
    throw new SecretKeyNotFoundException()

  def cnt = 0
  def workingDir = config.config.workingDir
  logger.info "Processing files in $workingDir"

  new File(workingDir).eachFile { file ->
    logger.info ">> $file"

    if( file.isFile() ) {
      logger.info "Processing $file records with file id "
      cnt = 0
    }
  }
}
catch( SecretKeyNotFoundException ex ) {
  logger.error("Encryption key not found in environment variable DG_SECRET_KEY")
}
catch( Exception ex ) {
  logger.error("Unknown error occured!?", ex)
}