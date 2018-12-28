package dg

import java.util.regex.Pattern
import java.util.regex.Matcher
import java.util.Calendar

import dg.CardRequest

// class used to parse card request in CSV format
// throws validation errors if fields have errors
class LineParser {

  static Pattern zipPattern = Pattern.compile("[a-zA-Z0-9- ]")

  // parses a comma seperated string into each field
  // expected line format is
  // <merc ref>,<amount>,<card>,<expiry month>,<expiry year>,<first name>,<last name>,<email>,<postal code>
  def parse( recordId, line ) {
    def req = new CardRequest(recordId: recordId)
    def tokens = line.trim().split(',')

    if( tokens.length < 9 )
      throw new MissingTokensException('Minimum number of tokens missing')

    // ------------ ref
    def token = tokens[0].trim()
    validateEmpty(token, 'ref')

    req.ref = token

    // ------------ amount
    token = tokens[1].trim()

    if( token.length() == 0 )
      throw new IllegalArgumentException('Missing [amount] field')
    else if( token.isNumber() == false )
      throw new IllegalArgumentException('[amount] field is not numeric')

    req.amount = token

    // ------------ cardNbr
    token = tokens[2].trim()

    if( token.length() == 0 )
      throw new IllegalArgumentException('Missing [cardNbr] field')
    else if( token.isNumber() == false )
      throw new IllegalArgumentException('[cardNbr] field is not numeric')
    else {
      if( validateLuhn(token) == false )
        throw new IllegalArgumentException('[cardNbr] field is invalid')
    }

    req.cardNbr = token

    // ------------ cardExpMth
    token = tokens[3].trim()

    if( token.length() == 0 )
      throw new IllegalArgumentException('Missing [cardExpMth] field')
    else if( token.isNumber() == false )
      throw new IllegalArgumentException('[cardExpMth] field is not numeric')
    else {
      def cardExpMth = token as int
      if( cardExpMth == 0 || cardExpMth > 12 )
        throw new IllegalArgumentException('[cardExpMth] field is invalid')
    }

    req.cardExpMth = token

    // ------------ cardExpYear
    token = tokens[4].trim()

    if( token.length() == 0 )
      throw new IllegalArgumentException('Missing [cardExpYear] field')
    else if( token.isNumber() == false )
      throw new IllegalArgumentException('[cardExpYear] field is not numeric')
    else {
      def cardExpYear = token as int
      def currentYear = Calendar.getInstance().get(Calendar.YEAR)

      if( (cardExpYear < currentYear - 10) || (cardExpYear > currentYear + 10) )
        throw new IllegalArgumentException('[cardExpYear] field is invalid')
    }

    req.cardExpYear = token

    // ------------ firstName
    token = tokens[5].trim()
    validateEmpty(token, 'firstName')

    req.firstName = token

    // ------------ lastName
    token = tokens[6].trim()
    validateEmpty(token, 'lastName')

    req.lastName = token

    // ------------ email
    token = tokens[7].trim()
    validateEmpty(token, 'email')

    req.email = token

    // ------------ zipCode
    token = tokens[8].trim()
    Matcher matcher = zipPattern.matcher(token);
    if( !matcher.find() )
      throw new IllegalArgumentException('[zipCode] field is invalid')

    req.zipCode = token

    return req
  }

  // Validates that a token is an empty string
  // throws ValueError if it is empty
  def validateEmpty( token, name ) {
    if( token.length() == 0 )
      throw new IllegalArgumentException('Missing [' + name + '] field')
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