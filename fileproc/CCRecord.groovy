// @author Dimitrios Gianninas
// card request
class CCRecord {
  String merchantRefNum
  String amount
  Card card
  Boolean settleWithAuth = true
  Profile profile
  BillingDetails billingDetails

  String toString() {
    return "CCRecord[ ref=$merchantRefNum, amt=$amount, $card ]"
  }
}