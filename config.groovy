// application config information
client {
  url = 'https://api.test.paysafe.com'
  apiKey = 'dGVzdF9hc3NsMTpCLXFhMi0wLTViZTg4MzJjLTAtMzAyYzAyMTQ2Y2Q4ZDUyZGRjY2E4ZWU4Y2U1Nzg0NTUwNWNlODBjZmNhYjIzYzYyMDIxNDBmYjAzMDBiMGJmOWE4Y2M2M2ZjMGI3ZDU4ZTJjMGMxYjY3MjQxMzA='
  workingDir = 'working'
  recordsToLoad = 100
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