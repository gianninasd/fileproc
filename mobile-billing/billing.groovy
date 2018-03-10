// @author Dimitrios Gianninas
// Sample Groovy script for reading a CSV file and sending emails

@Grab('org.apache.commons:commons-email:1.5')

import org.apache.commons.mail.*

// global variables
Date today = new Date()
String fileName = "rogers.csv"

println "\nGroovy Mobile Billing"
println "-------------------------------"
println "Arguments: $args"
println "Processing file [$fileName] on $today"
println "-------------------------------"

// load config file
// not using 'def' keyword or setting a type so it has global script scope
cfg = new Config("config.json")

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
    BillingRecord rec = new BillingRecord()
    rec.phone = elem[0]
    rec.firstName = elem[1]
    rec.lastName = elem[2]
    rec.amount = elem[3]
    
    println "\nRecord $RowCount.counter processing txn: $rec"
    sendEmail( rec )
  }
  catch( Exception ex ) {
    println "\nRecord $RowCount.counter unable to read line: " + ex
  }

  RowCount.increase()
}

// send email to a list of recipients
def sendEmail( BillingRecord rec ) {
  String message = loadEmailTemplate( rec )

  Email email = new SimpleEmail()
  email.setHostName(cfg.data.smtpHost)
  //email.setSmtpPort(587)
  email.setAuthenticator(new DefaultAuthenticator(cfg.data.smtpUser, cfg.data.smtpPassword))
  email.setSSLOnConnect(true)
  email.setFrom(cfg.data.emailFrom)
  email.setSubject(cfg.data.emailSubject)
  email.setMsg(message)
  email.addTo("jimmyg1975@gmail.com")
  email.send()
}

// load email and bind the record data and return the full text
def loadEmailTemplate( BillingRecord rec ) {
  def binding = [
    firstName: rec.firstName,
    lastName: rec.lastName,
    amount: 22
  ]

  def tpl = new File('email.template')
  def engine = new groovy.text.GStringTemplateEngine()
  def template = engine.createTemplate( tpl ).make( binding )
  return template.toString()
}

// utility class to keep track of the line we are at
class RowCount {
  static int counter = 0

  static void increase() {
    counter++
  }
}