package dg

// Holds data for card request
class CardRequest {
  
  def recordId = 0
  def guid = ''
  def ref = ''
  def amount = ''
  def cardNbr = ''
  def cardExpMth = ''
  def cardExpYear = ''
  def zipCode = ''
  def firstName = ''
  def lastName = ''
  def email = ''

  String toString() {
    return 'ref=' + ref + 
      ',amount=' + amount + 
      ',firstName=' + firstName + 
      ',lastName=' + lastName + 
      ',email=' + email + 
      ',cardNbr=XXXX' + cardNbr[12..cardNbr.length()-1]
  }
}