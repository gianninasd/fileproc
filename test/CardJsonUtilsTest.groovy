package dg

import dg.CardJsonUtils

class CardJsonUtilsTest extends GroovyTestCase {

  void test_RequestSimple() {
    def req = new CardRequest()
    req.ref = 'ref1'
    req.amount = '1500'
    req.firstName = 'Rick'
    req.lastName = 'Hunter'
    req.email = 'rick@sdf3.com'
    req.cardNbr = '4111222233334444'

    assertEquals('''{"merchantRefNum":"ref1","amount":"1500","settleWithAuth":false,"card":{"cardNum":"4111222233334444","cardExpiry":{"month":"","year":""},"cvv":""},"profile":{"firstName":"Rick","lastName":"Hunter","email":"rick@sdf3.com"},"billingDetails":{"street":"","street2":"","city":"","state":"","country":"","zip":"","phone":""}}''', CardJsonUtils.createJson(req))
  }

  void test_RequestWithPrevCust() {
    def req = new CardRequest()
    req.ref = 'ref1'
    req.amount = '1500'
    req.firstName = 'Rick'
    req.lastName = 'Hunter'
    req.email = 'rick@sdf3.com'
    req.cardNbr = '4111222233334444'
    req.previousCustomer = 'Y'

    assertEquals('''{"merchantRefNum":"ref1","amount":"1500","settleWithAuth":false,"card":{"cardNum":"4111222233334444","cardExpiry":{"month":"","year":""},"cvv":""},"profile":{"firstName":"Rick","lastName":"Hunter","email":"rick@sdf3.com"},"billingDetails":{"street":"","street2":"","city":"","state":"","country":"","zip":"","phone":""},"recurring":"RECURRING"}''', CardJsonUtils.createJson(req))
  }
}