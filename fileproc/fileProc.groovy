// @author Dimitrios Gianninas
// Sample Groovy script for reading a CSV file and calling an external REST API

@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')

import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

// global variables
Date today = new Date()
String fileName = "cc.csv"

println "\nGroovy File Processor"
println "-------------------------------"
println "Arguments: $args"
println "Processing file [$fileName] on $today"
println "-------------------------------"

// load config file
// not using 'def' keyword or setting a type so it has script scope
cfg = new Config("cc-config.json")

// open file
File file = new File(fileName)
//println "Raw dump:\n" + file.text + "\n"

// scroll thru each line
file.eachLine { line -> parseLine(line) }

// all done
println "\n-------------------------------"
println "Processed $RowCount.counter records"
println "-------------------------------"

// function for parsing each line and calling the API
def parseLine( String line ) {
  String [] elem = line.split(",")

  try {
    CCRecord rec = new CCRecord()
    rec.merchantRefNum = elem[0]
    rec.amount = elem[1]
    rec.card = new Card()
    rec.card.cardNum = elem[2]
    rec.card.cardExpiry = new CardExpiry()
    rec.card.cardExpiry.month = elem[3]
    rec.card.cardExpiry.year = elem[4]
    rec.profile = new Profile()
    rec.profile.firstName = elem[5]
    rec.profile.lastName = elem[6]
    rec.profile.email = elem[7]
    rec.billingDetails = new BillingDetails()
    rec.billingDetails.zip = elem[8]
    
    println "\nRecord $RowCount.counter processing txn: $rec"
    callAPI( rec )
  }
  catch( Exception ex ) {
    println "\nRecord $RowCount.counter unable to read line: " + ex
  }

  RowCount.increase()
}

// function to call Paysafe Acct Mgmt API
def callAPI( CCRecord rec ) {
  def http = new HTTPBuilder( 'https://api.test.paysafe.com/' )
  http.setHeaders([Authorization: cfg.data.apiKey])
  
  def postBody = JsonOutput.toJson(rec)

  try {
    http.request( POST ) {
      uri.path = "cardpayments/v1/accounts/$cfg.data.accountId/auths"
      requestContentType = JSON
      body = postBody

      response.success = { resp, json ->
        println "  POST Success for id $json.id with status: $json.status"
      }

      response.failure = { resp, json ->
        println "  POST Failed with status $resp.statusLine.statusCode: Error code $json.error.code for id: $json.id"
      }
    }
  }
  catch( Exception ex ) {
    println "  ERROR Unable to call API: " + ex
  }
}

// utility class to keep track of the line we are at
class RowCount {
  static int counter = 0

  static void increase() {
    counter++
  }
}