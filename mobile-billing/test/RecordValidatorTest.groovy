package test

import dg.bill.BillingRecord
import dg.bill.RecordValidator

/**
 * @author Dimitrios Gianninas
 * tests the RecordValidatorTest class
 */
class RecordValidatorTest extends GroovyTestCase {

  void testSuccess() {
    // create sample data
    BillingRecord rec = new BillingRecord()
    rec.phone = "5141112233"
    rec.firstName = "Lisa"
    rec.lastName = "Hayes"
    rec.amount = "1507 b"

    RecordValidator val = new RecordValidator()
    def errors = val.validate( rec )

    println "errors>> " + errors

    assertEquals(0, errors.size())
    //assertEquals("", x, actual)
  }
}