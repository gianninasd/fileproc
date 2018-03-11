// @author Dimitrios Gianninas
// mobile billing record, where the phone is the primary key
class BillingRecord {
  String phone 
  String firstName
  String lastName

  // the amount for the current month
  String amount 

  // the cost for each month
  def months = [:]

  String toString() {
    return "BillingRecord[ phone=$phone, name=$firstName $lastName, amt=$amount, months=$months ]"
  }
}