// Groovy script for reading a CSV file and storing data in a database

@GrabConfig(systemClassLoader=true)
@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('ch.qos.logback:logback-classic:1.2.3')
@Grab('mysql:mysql-connector-java:8.0.13')
@Grab('org.apache.commons:commons-dbcp2:2.5.0')

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import groovy.time.TimeCategory
import java.util.Date

import dg.SecretKeyNotFoundException
import dg.DupeFileException
import dg.FileService

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
  def workingDir = config.client.workingDir
  def service = new FileService(secretKey, config.db)

  while( true ) {
    logger.info "Processing files in $workingDir"

    new File(workingDir).eachFile { file ->
      if( file.isFile() ) {
        try {
          fullFileName = file.getName()
          fileName = service.extractFileName(fullFileName)
          fileId = service.create(workingDir, fullFileName)
          
          logger.info "Processing [$fullFileName] records with file id $fileId"
          def startTime = new Date()
          cnt = 0

          // loop thru each line in file, ignoring lines starting with a pound character
          file.eachLine() { line ->
            if( line[0] != '#' ) {
              service.storeRecord(fileId, line)
              cnt += 1
            }
          }

          def endTime = new Date()
          def duration = TimeCategory.minus(endTime, startTime)
          logger.info "Finished storing $cnt records for file id $fileId in $duration"
          service.createAck(workingDir,fileName,'0','File received')
        }
        catch( FileNotFoundException ex ) {
          logger.warn("File [$fullFileName] not found")
        }
        catch( DupeFileException ex ) {
          logger.warn("File [$fullFileName] already uploaded in the last 24 hrs")
          service.createAck(workingDir,fileName,'-1','Duplicate file')
        }
        catch( all ) {
          logger.error("Unknown error occured!?")
          service.createAck(workingDir,fileName,'-99','Unknown error')
        }
      }
    }

    logger.info('Going to sleep for 60s...')
    sleep(60000)
  }
}
catch( SecretKeyNotFoundException ex ) {
  logger.error("Encryption key not found in environment variable DG_SECRET_KEY")
}
catch( Exception ex ) {
  logger.error("Unknown error occured!?", ex)  
}