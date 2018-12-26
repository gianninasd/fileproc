// application config information
config {
  accountId = 1001289630
  url = 'https://api.test.paysafe.com/cardpayments/v1/accounts/'
  apiUser = 'test_assl1'
  apiPass = 'B-qa2-0-5be8832c-0-302c02146cd8d52ddcca8ee8ce57845505ce80cfcab23c6202140fb0300b0bf9a8cc63fc0b7d58e2c0c1b6724130'
  workingDir = 'working'
  maxThreads = 5
}

// database config information
db {
  url = 'jdbc:mysql://localhost:3306/fileproc'
  username = 'root'
  password = 'root'
  driverClassName = 'com.mysql.cj.jdbc.Driver'
  minIdle = 5
  maxIdle = 10
  maxOpenPreparedStatements = 100
}