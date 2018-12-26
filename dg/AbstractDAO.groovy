package dg

import org.apache.commons.dbcp2.BasicDataSource
import groovy.sql.Sql

// base class for all DAO with re-usable methods
abstract class AbstractDAO {
  static BasicDataSource ds = null

  // initializes and returns pooled data source
  static BasicDataSource getDataSource( def config ) {
    if( ds == null ) {
      ds = new BasicDataSource()
      ds.url = config.url
      ds.username = config.username
      ds.password = config.password
      ds.driverClassName = config.driverClassName
      ds.minIdle = config.minIdle
      ds.maxIdle = config.maxIdle
      ds.maxOpenPreparedStatements = config.maxOpenPreparedStatements
    }

    return ds
  }

  // executes INSERT database operation using the SQL statement and data provided
  def insert( sqlStmt, data ) {
    Sql sql = null
    def id = 0

    try {
      def ds = getDataSource(config)
      sql = new Sql(ds)
      def result = sql.executeInsert(sqlStmt, data)
      id = result[0][0]
    }
    finally {
      sql.close() // returns connection to the pool
    }

    return id
  }
}