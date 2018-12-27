Groovy File Processor
================
A Groovy program that reads a CSV file, stores each record in a database and then for each record, calls a REST API that performs a card payment.

*Sending emails with Apache Commons Email*

## Pre-requisites
* Install Java 8
* Install Groovy 2.5.x 

## Getting started
There two seperate applications you need to launch, the Loader and the Processor, first get the code:
* `git clone https://github.com/gianninasd/fileproc.git`
* `cd fileproc`

Now follow these steps to launch both applications:
* Open a console and run `groovy fileLoader.groovy` ... it will monitor for csv files in the `working` sub-folder

You will see processing output on your console and in a file called `logs/fileProc.log`

## References
Below are some reference web sites
* Docs: http://groovy-lang.org/documentation.html
* Tutorials:
  * https://mrhaki.blogspot.com/
  * https://www.youtube.com/watch?v=fHhf1xG0pIA&t=72s
  * https://www.baeldung.com/java-future
* GMail SMTP setup: https://support.google.com/a/answer/176600?hl=en
