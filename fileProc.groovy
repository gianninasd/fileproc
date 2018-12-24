// Sample Groovy script for reading a CSV file and calling an external REST API

@GrabConfig(systemClassLoader=true)
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('ch.qos.logback:logback-classic:1.2.3')

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.util.logging.Slf4j
/*import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.* */

// global variables
Date today = new Date()
String fileName = "cc.csv"

def logger = LoggerFactory.getLogger('fileProc')
logger.info "Groovy File Processor"
logger.info "Arguments: $args"
logger.info "Processing file [$fileName] on $today"
logger.info "-------------------------------"