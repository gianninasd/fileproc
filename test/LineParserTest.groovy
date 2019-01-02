package test

import dg.LineParser
import dg.MissingTokensException

class LineParserTest extends GroovyTestCase {

  def parser = new LineParser()

  void testGoodLine() {
    parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
  }

  void testMissingTokens() {
    shouldFail MissingTokensException, {
      parser.parse(0,'4111111111111111,10,2020')
    }
  }

  void testMissingRef() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadAmount() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,xxx,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }    
  }

  void testBadCardNbr() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'xxx,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadCardBrand() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,XX,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadCvv() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,ab,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadCardExpMth() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,15/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadCardExpYear() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,15/95,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadTxnType() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,15/95,1500,Y,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadAcctNbr() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,15/95,1500,P,abvc,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadFirstName() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadLastName() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadPhone() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,,,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadEmail() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadAddr1() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')
    }
  }

  void testBadAddr2() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,irhgigbwrbgirwbgbgbwibrgbbiwbvgrgvbiwbvvgvuy0123rnonqgbu45gtg,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,US')      
    }
  }

  void testBadCity() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,,CA,M5H 2N2,US')
    }
  }

  void testBadProvince() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,XXX,M5H 2N2,US')
    }
  }

  void testBadCountry() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,M5H 2N2,XXX')
    }
  }

  void testBadZipCode() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,!!,US')
    }
  }

  void testBadPrevCust() {
    shouldFail IllegalArgumentException, {
      parser.parse(0,'4111111111111111,VI,123,10/20,1500,P,1001289630,jim7025,Rick,Hunter,123 broadway,,514-111-2233,rick@sdf3.com,Irvine,CA,!M5H 2N2,US,X')
    }
  }
}