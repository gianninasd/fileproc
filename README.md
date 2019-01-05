Groovy File Processor
================
A Groovy program that reads a CSV file, stores each record in a database and then for each record, calls a REST API that performs a card payment.

*Sending emails with Apache Commons Email*

## Pre-requisites
* Install and run MySQL 5.5 or higher
* Install Java 8
* Install Groovy 2.5.x 

## Running applications
There two seperate applications you need to launch, the Loader and the Processor, first get the code:
* `git clone https://github.com/gianninasd/fileproc.git`
* `cd fileproc`

Now follow these steps to launch both applications:
* Open a console and run `groovy fileLoader.groovy` ... it will monitor for csv files in the `working` sub-folder
* Open a second console and run `groovy fileProc.groovy` ... it will monitor the database for records in INITIAL status

You will see processing output on your console and in a file called `logs/fileProc.log`

To execute all unit tests from the root folder, from the console run: `groovy allTests.groovy`

## References
Below are some reference web sites
* Docs: http://groovy-lang.org/documentation.html
* Tutorials:
  * https://www.tutorialspoint.com/groovy/index.htm
  * https://mrhaki.blogspot.com/
  * https://www.youtube.com/watch?v=fHhf1xG0pIA&t=72s
  * https://www.baeldung.com/java-future
  * http://java-regex-tester.appspot.com/
* GMail SMTP setup: https://support.google.com/a/answer/176600?hl=en
* Http Builder: https://github.com/jgritman/httpbuilder
