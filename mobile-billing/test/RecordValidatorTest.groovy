package test

import dg.bill.BillingRecord
import dg.bill.RecordValidator

/**
 * @author Dimitrios Gianninas
 * tests the RecordValidatorTest class
 */
class RecordValidatorTest extends GroovyTestCase {

  RecordValidator val = new RecordValidator()
  BillingRecord rec = new BillingRecord()

  void testSuccess() {
    rec.phone = "5141112233"
    rec.firstName = "Lisa"
    rec.lastName = "Hayes"
    rec.amount = "1507"

    def errors = val.validate( rec )
    assertEquals(0, errors.size())
  }

  void testBadPhone() {
    rec.phone = "5141aa2233"
    rec.firstName = "Lisa"
    rec.lastName = "Hayes"
    rec.amount = "1507"

    def errors = val.validate( rec )
    assertEquals("bad phone: 5141aa2233", errors.get(0))
  }

  void testBadName() {
    rec.phone = "5141112233"
    rec.firstName = ""
    rec.lastName = "HayesLorem ipsum dolor sit amet dolor Lorem ipsum dolor sit amet"
    rec.amount = "1507"

    def errors = val.validate( rec )
    assertEquals("bad firstName: ", errors.get(0))
    assertEquals("bad lastName: HayesLorem ipsum dolor sit amet dolor Lorem ipsum dolor sit amet", errors.get(1))
  }

  void testBadAmount() {
    rec.phone = "5141112233"
    rec.firstName = "Lisa"
    rec.lastName = "Hayes"
    rec.amount = "a.b"

    def errors = val.validate( rec )
    assertEquals("bad amount: a.b", errors.get(0))
  }
}