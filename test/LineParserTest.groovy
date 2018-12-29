package test

import dg.LineParser
import dg.MissingTokensException

class LineParserTest extends GroovyTestCase {

  def parser = new LineParser()

  void testGoodLine() {
    parser.parse(0,'jim7025,1500,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
  }

  void testMissingTokens() {
    shouldFail MissingTokensException, {
      parser.parse(0,'jim7025,1500,4111111111111111')
    }
  }

  void testMissingRef() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,',1500,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
    }
  }

  void testBadAmount() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,xx,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
    }    
  }

  void testBadCardNbr() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,1500,xxxxxx,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
    }
  }

  void testBadCardExpMth() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,1500,4111111111111111,15,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
    }
  }

  void testBadCardExpYear() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,1500,4111111111111111,10,1995,Rick,Hunter,rick@sdf3.com,M5H 2N2')
    }
  }

  void testBadFirstName() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,,Hunter,rick@sdf3.com,M5H 2N2')
    }
  }

  void testBadLastName() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,Rick,,rick@sdf3.com,M5H 2N2')
    }
  }

  void testBadEmail() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,Rick,Hunter,,M5H 2N2')
    }
  }

  void testBadZipCode() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,!!')
    }
  }
}