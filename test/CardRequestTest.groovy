package test

import dg.CardRequest
import dg.LineParser
import dg.MissingTokensException

class CardRequestTest extends GroovyTestCase {

  void test_ToString() {
    def req = new CardRequest()
    req.ref = 'ref1'
    req.amount = '1500'
    req.firstName = 'Rick'
    req.lastName = 'Hunter'
    req.email = 'rick@sdf3.com'
    req.cardNbr = '4111222233334444'
    assertEquals(req.toString(), 'ref=ref1,amount=1500,firstName=Rick,lastName=Hunter,email=rick@sdf3.com,cardNbr=XXXX4444')
  }
}