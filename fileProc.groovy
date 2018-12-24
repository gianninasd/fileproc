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
import groovy.sql.Sql
import org.apache.commons.dbcp2.BasicDataSource
/*import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.* */

// global variables
Date today = new Date()
String fileName = "cc.csv"

def logger = LoggerFactory.getLogger('fileProc')
logger.info('Groovy ' + GroovySystem.version + ' File Processor running on ' + System.properties['os.name'] + ' ' + System.properties['os.version'])
logger.info "Arguments: $args"
logger.info "Processing file [$fileName] on $today"
logger.info "-------------------------------"

// load application config
ConfigObject config = new ConfigSlurper().parse(new File('config.groovy').toURI().toURL())

def ds = new BasicDataSource()
ds.url = config.db.url
ds.username = config.db.username
ds.password = config.db.password
ds.driverClassName = config.db.driverClassName
ds.minIdle = 5 
ds.maxIdle = 10
ds.maxOpenPreparedStatements = 100

def sql = new Sql(ds)
sql.eachRow('select record_id, creation_date, raw_record from fileproc.file_records where status_cde = "ERROR" order by creation_date asc limit 10') { row ->
  logger.info 'ds>> ' + row.raw_record
}
sql.close()