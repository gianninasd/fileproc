package dg

import dg.AbstractDAO
import groovy.sql.Sql

// DAO to interac with files
class FileDAO extends AbstractDAO {

  ConfigObject config

  // sql statement constants
  static def CREATE = "insert into file_uploads (filename,filehash,creation_date,modification_date) " \
    + "values (:filename,:filehash,now(),now())"
  static def FIND_LAST24HRS = "select count(1) as cnt from fileproc.file_uploads where filehash = :filehash " \
    + "and creation_date >= NOW() - INTERVAL 1 DAY"

  // create a new file record entry, returning the file ID
  def create( fileName, fileHash ) {
    def data = [
      'filename': fileName,
      'filehash': fileHash
    ]
    Sql sql = null
    def id = 0

    try {
      def ds = getDataSource(config)
      sql = new Sql(ds)
      def result = sql.executeInsert(CREATE, data)
      id = result[0][0]
    }
    finally {
      sql.close()
    }

    return id
  }

  // returns the number of times the filehash occurs in the last 24 hrs
  def countInLast24hrs( fileHash ) {
    def data = ['filehash': fileHash]
    Sql sql = null
    def cnt = 0
    
    try {
      def ds = getDataSource(config)
      sql = new Sql(ds)
      def result = sql.firstRow(FIND_LAST24HRS, data)
      cnt = result['cnt']
    }
    finally {
      sql.close()
    }

    return cnt
  }
}