package dg.bill

import groovy.text.*
import groovy.text.markup.*
import org.apache.commons.mail.*
import dg.bill.BillingRecord

/**
 * @author Dimitrios Gianninas
 * prepares the email content using an HTML template file and sends the email using Apache Commons Mail
 */
class EmailSender {

  private String smtpHost
  private String smtpUser
  private String smtpPassword
  
  String [] allMonths // list of all standard months as a 3 letter code
  String emailFrom
  String emailSubject

  // class constructor
  public EmailSender( String smtpHost, String smtpUser, String smtpPassword ) {
    this.smtpHost = smtpHost
    this.smtpUser = smtpUser
    this.smtpPassword = smtpPassword
  }

  // send email to a list of recipients
  def send( BillingRecord rec ) {
    String message = generateEmailContent( rec )

    Email email = new SimpleEmail()
    email.setHostName(smtpHost)
    email.setAuthenticator(new DefaultAuthenticator(smtpUser, smtpPassword))
    email.setSSLOnConnect(true)
    email.setFrom(this.emailFrom)
    email.setSubject(this.emailSubject)
    email.setContent(message,"text/html")
    email.addTo("jimmyg1975@gmail.com")
    email.send()
  }

  // load email template and bind the record data and return the full text
  private def generateEmailContent( BillingRecord rec ) {
    List orderedMonths = orderMonthValue(rec.months)
    
    def model = [
      pageTitle: this.emailSubject,
      firstName: rec.firstName,
      lastName: rec.lastName,
      months: orderedMonths,
      allMonths: allMonths
    ]

    TemplateConfiguration tplCfg = new TemplateConfiguration()        
    MarkupTemplateEngine engine = new MarkupTemplateEngine(tplCfg)    

    Template template = engine.createTemplateByPath("email.template")
    Writable output = template.make(model)
    StringWriter writer = new StringWriter()
    output.writeTo(writer);

    return writer.toString()
  }

  // orders the monthly data for display
  private def orderMonthValue( Map months ) {
    def mths = []

    allMonths.each{
      if( months.containsKey(it) ) {
        mths.add(months.get(it) as Double)
      }
      else {
        mths.add(0.0)
      }      
    }

    return mths
  }
}