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

import dg.RecordDAO

// init logger
def logger = LoggerFactory.getLogger('fileProc')

def osName = System.properties['os.name']
def osVersion = System.properties['os.version']
logger.info "Groovy $GroovySystem.version File Processor running on $osName $osVersion"

// load application config
ConfigObject config = new ConfigSlurper().parse(new File('config.groovy').toURI().toURL())

// TODO decrypt
// TODO line parser
// TODO multi-threading

RecordDAO recordDAO = new RecordDAO(config: config.db)
def recs = recordDAO.getAllWithStatusInitial()

logger.info "Processing ${recs.size()} record(s)"

for(rec in recs) {
  logger.info "ds1>> $rec.rawData"
}