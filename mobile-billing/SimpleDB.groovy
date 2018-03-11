// @author Dimitrios Gianninas
// Representation of an in-memory database of all mobile billing records
// Allows for updates and storing to file for later re-loading

import groovy.json.JsonOutput

class SimpleDB extends Config {

  String fileName

  // class the super contructor to load the file into memory
  public SimpleDB( String fileName ) {
    super( fileName )
    this.fileName = fileName
  }

  // update the in memory DB with the currently processing record
  def update( String month, BillingRecord rec ) {
    BillingRecord rec2Update

    if( this.data.db.containsKey( rec.phone ) ) {
      // if contains the record, update it
      rec2Update = this.data.db.get( rec.phone )
      println "Found existing record"

      rec2Update.firstName = rec.firstName
      rec2Update.lastName = rec.lastName
    }
    else {
      // if doesn't contain the record, add it
      rec2Update = rec
      println "Adding new record"
    }

    // update the amount for a month
    rec2Update.months[month] = rec.amount

    // store it
    this.data.db.put( rec2Update.phone, rec2Update )    
  }

  // stores the in-memory DB map to file in JSON format
  def writeToFile() {
    DBWrapper rootObj = new DBWrapper()
    rootObj.db = this.data.db

    String rootObjAsJson = JsonOutput.toJson(rootObj)

    new File( fileName ).withWriter('utf-8') { 
        writer -> writer.writeLine rootObjAsJson 
    } 
  }

  // utility class so that we write the in-memory DB to file as JSON
  class DBWrapper {
    Object db
  }
}