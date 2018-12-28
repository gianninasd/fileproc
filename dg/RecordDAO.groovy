package dg

import dg.AbstractDAO
import groovy.sql.Sql

// Holds data for a raw record
class Record {
  def recordId = 0
  def rawData = ''
}

// DAO to interac with file records
class RecordDAO extends AbstractDAO {

  // sql statement constants
  static def CREATE = 'insert into file_records (file_id,status_cde,raw_record,creation_date,modification_date) ' \
    + 'values (:fileId,:status,:rawRecord,now(),now())'
  static def UPDATE_SENT = 'update file_records set status_cde = :status, modification_date = now(), ' \
    + 'guid = :guid, request_body = :request where record_id = :recordId'
  static def UPDATE_RESPONSE = 'update file_records set status_cde = :status, response_body = :response, ' \
    + 'modification_date = now() where record_id = :recordId'
  static def LOAD_ALL_INITIAL = 'select record_id, creation_date, raw_record from fileproc.file_records ' \
    + 'where status_cde = "INITIAL" order by creation_date asc limit 10'

  ConfigObject config

  // create a new record entry
  def createInitial( fileId, record ) {
    def data = [
      'fileId': fileId,
      'status': 'INITIAL',
      'rawRecord': record
    ]

    insert(CREATE, data)
  }

  // update an existing record status
  def updateSent( rec ) {
    def data = [
      'recordId': rec.recordId,
      'status': 'SENT',
      'guid': rec.guid,
      'request': rec.toString()
    ]

    update(UPDATE_SENT, data)
  }

  // update an existing record
  def updateResponse( rec ) {
    def data = [
      'recordId': rec.recordId,
      'status': rec.decision,
      'response': rec.toString()
    ]

    update(UPDATE_RESPONSE, data)
  }

  // returns all the records in an initial status
  def getAllWithStatusInitial() {
    Sql sql = null

    try {
      ds = getDataSource(config)
      sql = new Sql(ds)
      def recs = []

      sql.eachRow(LOAD_ALL_INITIAL) { row ->
        Record rawRec = new Record()
        rawRec.recordId = row.record_id
        rawRec.rawData = row.raw_record
        recs.add rawRec
      }

      return recs
    }
    finally {
      sql.close()
    }
  }
}