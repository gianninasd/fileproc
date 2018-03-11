// @author Dimitrios Gianninas
// Sample Groovy script for reading a CSV file and sending emails

@Grab('org.apache.commons:commons-email:1.5')

import groovy.text.*
import groovy.text.markup.*
import org.apache.commons.mail.*
import groovy.json.JsonOutput

// global variables
// not using 'def' keyword or setting a type so it has global script scope
today = new Date()
months = ["jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"]
db = [:] // the in-memory database map of records

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

storeDBToFile()

// stores the in-memory DB map to file in JSON format
def storeDBToFile() {
  DBWrapper rootObj = new DBWrapper()
  rootObj.db = db

  String rootObjAsJson = JsonOutput.toJson(rootObj)

  println "\nrootObjAsJson: $rootObjAsJson"

  new File( cfg.data.dbFile ).withWriter('utf-8') { 
      writer -> writer.writeLine rootObjAsJson 
  } 
}

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

    updateInMemoryDB( rec )

    // send the email to the user and his manager
    sendEmail( rec )
  }
  catch( Exception ex ) {
    println "\nRecord $RowCount.counter unable to read line: " + ex
  }

  RowCount.increase()
}

// update the in memory DB with the currently processing record
def updateInMemoryDB( BillingRecord rec ) {
  if( db.containsKey( rec.phone ) ) {
    // if contains the record, update it
    BillingRecord dbRec = db.get( rec.phone )
    println "\ndbRec: $dbRec"
  }
  else {
    // if doesn't contain the record, add it
    rec.updateAmountForMonth( cfg.data.month )
    db.put( rec.phone, rec )
  }
  
  println "\ndb: $db"
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

// utility class so that we write the in-memory DB to file as JSON
class DBWrapper {
  Object db
}