package dg

// Holds data for card request
class CardRequest {
  
  def recordId = 0
  def guid = ''
  def txnType = ''
  def accountId = ''
  def ref = ''
  def amount = ''
  def cardNbr = ''
  def cardBrand = ''
  def cvv = ''
  def cardExpMth = ''
  def cardExpYear = ''
  def addr1 = ''
  def addr2 = ''
  def city = ''
  def province = ''
  def country = ''
  def zipCode = ''
  def firstName = ''
  def lastName = ''
  def email = ''
  def phone = ''
  def previousCustomer = ''

  String toString() {
    return 'ref=' + ref + 
      ',amount=' + amount + 
      ',firstName=' + firstName + 
      ',lastName=' + lastName + 
      ',email=' + email + 
      ',cardNbr=XXXX' + cardNbr[12..cardNbr.length()-1]
  }
}