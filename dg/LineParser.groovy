package dg

import java.util.regex.Pattern
import java.util.regex.Matcher
import java.util.Calendar

import dg.CardRequest

// class used to parse card request in CSV format
// throws validation errors if fields have errors
class LineParser {

  static def BRANDS = ['VI','MC','AM','DC','DI']
  static def TXN_TYPES = ['A','P']
  static def PREV_CUST = ['Y','N']
  static Pattern zipPattern = Pattern.compile("[a-zA-Z0-9- ]")

  /** parses a comma seperated string into each field, expected line format is
   * card 1..20
   * card brand 1..2 
   * CVV 1..4 
   * expiry month 1..5
   * expiry year
   * amount 1..13
   * tran type 1
   * acct nbr 1..13
   * merc ref 1..40
   * first name 1..40
   * last name 1..40
   * addr1 1..50
   * addr2 1..50
   * phone 1..40
   * email 1..100
   * city 1..40
   * province 1..2
   * postal code 1..10
   * country 1..2
   * previous cust 1..1 (optional)
   */
  def parse( recordId, line ) {
    def req = new CardRequest(recordId: recordId)
    def tokens = line.trim().split(',')

    if( tokens.length < 18 )
      throw new MissingTokensException('Minimum number of tokens missing')

    // ------------ cardNbr
    def token = tokens[0].trim()
    validateLength(token, 'cardNbr', 1, 20)

    if( token.isNumber() == false )
      throw new IllegalArgumentException('[cardNbr] field is not numeric')
    else {
      if( validateLuhn(token) == false )
        throw new IllegalArgumentException('[cardNbr] field is invalid')
    }

    req.cardNbr = token

    // ------------ cardBrand
    token = tokens[1].trim()
    
    if( (token in BRANDS) == false )
       throw new IllegalArgumentException('[cardBrand] field does not have a valid value')

    req.cardBrand = token

    // ------------ cvv
    token = tokens[2].trim()
    validateLength(token, 'cvv', 1, 4)
    
    if( token.isNumber() == false )
      throw new IllegalArgumentException('[cvv] field is not numeric')

    req.cvv = token

    // ------------ cardExpMth
    token = tokens[3].trim()

    def slashIdx = token.indexOf('/')
    def mth = token[0..slashIdx-1]
    def yr = token[slashIdx+1..token.length()-1]
    def cardExpYear = 0

    if( mth.isNumber() == false )
      throw new IllegalArgumentException('[cardExpMth] field is not numeric')
    else {
      def cardExpMth = mth as int
      if( cardExpMth == 0 || cardExpMth > 12 )
        throw new IllegalArgumentException('[cardExpMth] field is invalid')
    }

    req.cardExpMth = mth

    // ------------ cardExpYear
    if( yr.isNumber() == false )
      throw new IllegalArgumentException('[cardExpYear] field is not numeric')
    else {
      cardExpYear = (yr as int) + 2000 // TODO not sure this is correct way of doing it
      def currentYear = Calendar.getInstance().get(Calendar.YEAR)

      if( (cardExpYear < currentYear - 10) || (cardExpYear > currentYear + 10) )
        throw new IllegalArgumentException('[cardExpYear] field is invalid')
    }

    req.cardExpYear = cardExpYear

    // ------------ amount
    token = tokens[4].trim()
    validateLength(token, 'amount', 1, 13)

    if( token.isNumber() == false )
      throw new IllegalArgumentException('[amount] field is not numeric')

    req.amount = token

    // ------------ txnType
    token = tokens[5].trim()

    if( (token in TXN_TYPES) == false )
       throw new IllegalArgumentException('[txnType] field does not have a valid value')

    req.txnType = token

    // ------------ acctNbr
    token = tokens[6].trim()
    validateLength(token, 'acctNbr', 1, 10)

    if( token.isNumber() == false )
      throw new IllegalArgumentException('[acctNbr] field is not numeric')

    req.acctNbr = token

    // ------------ ref
    token = tokens[7].trim()
    validateLength(token, 'ref', 1, 40)

    req.ref = token

    // ------------ firstName
    token = tokens[8].trim()
    validateLength(token, 'firstName', 1, 40)

    req.firstName = token

    // ------------ lastName
    token = tokens[9].trim()
    validateLength(token, 'firstName', 1, 40)

    req.lastName = token

    // ------------ addr1
    token = tokens[10].trim()
    validateLength(token, 'addr1', 1, 50)

    req.addr1 = token

    // ------------ addr2
    token = tokens[11].trim()
    validateLength(token, 'addr2', 0, 50)

    req.addr2 = token

    // ------------ phone
    token = tokens[12].trim()
    validateLength(token, 'phone', 1, 40)

    req.phone = token

    // ------------ email
    token = tokens[13].trim()
    validateLength(token, 'email', 1, 100)

    req.email = token

    // ------------ city
    token = tokens[14].trim()
    validateLength(token, 'city', 1, 40)

    req.city = token

    // ------------ province
    token = tokens[15].trim()
    validateLength(token, 'province', 1, 2)

    req.province = token

    // ------------ zipCode
    token = tokens[16].trim()
    validateLength(token, 'zipCode', 1, 10)

    Matcher matcher = zipPattern.matcher(token);
    if( !matcher.find() )
      throw new IllegalArgumentException('[zipCode] field is invalid')

    req.zipCode = token

    // ------------ country
    token = tokens[17].trim()
    validateLength(token, 'country', 1, 2)

    req.country = token

    // ------------ previousCustomer
    if( tokens.length == 19 ) {
      token = tokens[18].trim()

      if( (token in PREV_CUST) == false )
        throw new IllegalArgumentException('[previousCustomer] field does not have a valid value')

      req.previousCustomer = token
    }    

    return req
  }

  // Validates that a token is within certain length boundaries
  // throws IllegalArgumentException if not within those boundaries
  def validateLength( token, name, min, max ) {
    if( token.length() < min || token.length() > max )
      throw new IllegalArgumentException("Bad field length for [$name]")
  }

  // Validates that a card passes the Luhn check
  // see https://en.wikipedia.org/wiki/Luhn_algorithm
  def validateLuhn( ccNumber ) {
    int sum = 0;
    boolean alternate = false;
    
    for (int i = ccNumber.length() - 1; i >= 0; i--) {
      int n = Integer.parseInt(ccNumber.substring(i, i + 1));
      if (alternate) {
        n *= 2;
        if (n > 9) {
          n = (n % 10) + 1;
        }
      }
      sum += n;
      alternate = !alternate;
    }

    return (sum % 10 == 0);
  }
}