// @author Dimitrios Gianninas
// Sample Groovy script for reading a CSV file and sending emails

@Grab('org.apache.commons:commons-email:1.5')

import groovy.text.*
import groovy.text.markup.*
import org.apache.commons.mail.*

// global variables
// not using 'def' keyword or setting a type so it has global script scope
today = new Date()
months = ["jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"]

println "\nGroovy Mobile Billing"
println "-------------------------------"
println "Arguments: $args"

try {
  validateCommandArguments( args )
}
catch( Exception ex ) {
  println "\nSTOPPED - " + ex.message
  System.exit(-1)
}

// load config file
cfg = new Config("config.json")

// add the command line arguments
cfg.data.month = args[0]
cfg.data.sourceFile = args[1]
cfg.data.dbFile = args[2]

// load the previously store DB file into memory
simpleDB = new SimpleDB( cfg.data.dbFile )
println "\n simpleDB: $simpleDB.data \n"

println "Processing file [$cfg.data.sourceFile] on $today"
println "-------------------------------"

// open file
File file = new File(cfg.data.sourceFile)
//println "Raw dump:\n" + file.text + "\n"

// scroll thru each line
file.eachLine { line -> parseLine(line) }

// all done
println "\n-------------------------------"
println "Processed $RowCount.counter records"
println "-------------------------------"

simpleDB.writeToFile()

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

    simpleDB.update( cfg.data.month, rec )
    sendEmail( rec )
  }
  catch( Exception ex ) {
    println "\nRecord $RowCount.counter unable to read line: " + ex
  }

  RowCount.increase()
}

// send email to a list of recipients
def sendEmail( BillingRecord rec ) {
  String message = getEmailContent( rec )

  Email email = new SimpleEmail()
  email.setHostName(cfg.data.smtpHost)
  email.setAuthenticator(new DefaultAuthenticator(cfg.data.smtpUser, cfg.data.smtpPassword))
  email.setSSLOnConnect(true)
  email.setFrom(cfg.data.emailFrom)
  email.setSubject(cfg.data.emailSubject)
  email.setContent(message,"text/html")
  email.addTo("jimmyg1975@gmail.com")
  email.send()
}

// load email template and bind the record data and return the full text
def getEmailContent( BillingRecord rec ) {
  def model = [
    pageTitle: cfg.data.emailSubject,
    firstName: rec.firstName,
    lastName: rec.lastName
  ]

  TemplateConfiguration tplCfg = new TemplateConfiguration()        
  MarkupTemplateEngine engine = new MarkupTemplateEngine(tplCfg)    

  Template template = engine.createTemplateByPath("email.template")
  Writable output = template.make(model)
  StringWriter writer = new StringWriter()
  output.writeTo(writer);

  return writer.toString()
}

// parses the command line arguments and returns and error if some are missing or invalid
def validateCommandArguments( String[] arguments ) {
  if( arguments.length == 0 || arguments.length != 3 ) {
    throw new Exception("Missing one or more arguments: <month> <source file> <database file>")
  }

  String argMonth = arguments[0]

  if( !months.contains(argMonth) ) {
    throw new Exception("First argument must be a valid 3 letter month")
  }
}

// utility class to keep track of the line we are at
class RowCount {
  static int counter = 0

  static void increase() {
    counter++
  }
}