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