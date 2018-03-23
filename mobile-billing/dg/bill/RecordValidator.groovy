package dg.bill

/**
 * @author Dimitrios Gianninas
 * validates a BillingRecord
 */
class RecordValidator {

  def validate( BillingRecord rec ) {
    def errors = []

    if( !rec.phone.isNumber() ) {
      errors.add("bad phone: " + rec.phone)
    }

    if( rec.firstName.size() == 0 || rec.firstName.size() > 40 ) {
      errors.add("bad firstName: " + rec.firstName)
    }

    if( rec.lastName.size() == 0 || rec.lastName.size() > 40 ) {
      errors.add("bad lastName: " + rec.lastName)
    }

    if( !rec.amount.isDouble() ) {
      errors.add("bad amount: " + rec.amount)
    }

    return errors
  }
}