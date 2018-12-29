package test

import dg.CardResponse

class CardResponseTest extends GroovyTestCase {

  void test_ToString_Success() {
    def res = new CardResponse(0,'SUCCESS','guid1234')
    res.txnId = '0011'
    assertEquals(res.toString(), 'guid1234 SUCCESS id: 0011')
  }
  
  void test_ToString_Failed() {
    def res = new CardResponse(0,'FAILED','guid1234')
    res.txnId = '0011'
    res.errorCode = '1007'
    res.message = 'insufficient funds'
    assertEquals(res.toString(), 'guid1234 FAILED id: 0011 Error code: 1007 - insufficient funds')
  }
}