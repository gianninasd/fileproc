package dg.bill

/**
 * @author Dimitrios Gianninas
 * validates a BillingRecord
 */
class RecordValidator {

  def validate( BillingRecord rec ) {
    def errors = []

    if( !rec.amount.isDouble() ) {
      errors.add("bad amount: " + rec.amount)
    }

    return errors
  }
}