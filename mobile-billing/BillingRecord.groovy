// @author Dimitrios Gianninas
// billing record
class BillingRecord {
  String phone
  String firstName
  String lastName
  String amount  

  String toString() {
    return "BillingRecord[ phone=$phone, name=$firstName $lastName, amt=$amount ]"
  }
}