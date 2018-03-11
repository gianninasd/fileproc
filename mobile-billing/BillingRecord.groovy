// @author Dimitrios Gianninas
// mobile billing record
class BillingRecord {
  // the primary key
  String phone 
  String firstName
  String lastName

  // the amount for the current month
  String amount 

  // the cost for each month
  def months = [:]

  // update the billing amount for the specific month
  def updateAmountForMonth( String month ) {
    months[month] = amount
    amount = "" // clear the current amount
  }

  String toString() {
    return "BillingRecord[ phone=$phone, name=$firstName $lastName, amt=$amount, months=$months ]"
  }

  /*public boolean equals( Object other ) {
    return this.phone == other.phone
  }

  public int hashCode() {
    return 1
  }*/
}