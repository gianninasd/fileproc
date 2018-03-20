package dg.bill

import groovy.json.JsonSlurper

/** 
 * @author Dimitrios Gianninas
 * Sample Groovy script for reading a JSON config file
 */
class Config {

  public def data

  public Config( String configFile ) {
    try {
      def slurper = new JsonSlurper()
      this.data = slurper.parse( new File(configFile) )
    }
    catch( Exception ex ) {
      println "ERROR Unable to load file [$configFile]: " + ex
    }
  }
}