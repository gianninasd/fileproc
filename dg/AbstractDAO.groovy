package dg

import org.apache.commons.dbcp2.BasicDataSource

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
}