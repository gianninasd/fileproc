// @author Dimitrios Gianninas
// Sample Groovy script for reading a CSV file and sending emails

@Grab('org.apache.commons:commons-email:1.5')

import dg.bill.BillingRecord
import dg.bill.Config
import dg.bill.SimpleDB
import dg.bill.EmailSender

// global variables
// not using 'def' keyword or setting a type so it has global script scope
today = new Date()
MONTHS = ["jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"]

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

// initialize email sender
emailSender = new EmailSender( cfg.data.smtpHost, cfg.data.smtpUser, cfg.data.smtpPassword )
emailSender.allMonths = MONTHS
emailSender.emailFrom = cfg.data.emailFrom
emailSender.emailSubject = cfg.data.emailSubject

// load the previously store DB file into memory
simpleDB = new SimpleDB( cfg.data.dbFile )

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

    BillingRecord updatedRec = simpleDB.update( cfg.data.month, rec )
    emailSender.send( updatedRec )
  }
  catch( Exception ex ) {
    println "\nRecord $RowCount.counter unable to read line: " + ex
  }

  RowCount.increase()
}

// parses the command line arguments and returns and error if some are missing or invalid
def validateCommandArguments( String[] arguments ) {
  if( arguments.length == 0 || arguments.length != 3 ) {
    throw new Exception("Missing one or more arguments: <month> <source file> <database file>")
  }

  String argMonth = arguments[0]

  if( !MONTHS.contains(argMonth) ) {
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