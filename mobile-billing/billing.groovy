// @author Dimitrios Gianninas
// Sample Groovy script for reading a CSV file and sending emails

// global variables
Date today = new Date()
String fileName = "rogers.csv"

println "\nGroovy Mobile Billing"
println "-------------------------------"
println "Arguments: $args"
//println "Processing file [$fileName] on $today"
println "-------------------------------"


// open file
File file = new File(fileName)
println "Raw dump:\n" + file.text + "\n"