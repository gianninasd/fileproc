package test

import dg.LineParser
import dg.MissingTokensException

class LineParserTest extends GroovyTestCase {

  def parser = new LineParser()

  void testGoodLine() {
    try {
      parser.parse(0,'jim7025,1500,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
    }
    catch( Exception ex ) {
      throw new Exception('Was not excepting it to raise an Exception', ex)
    }
  }

  void testMissingTokens() {
    try {
      parser.parse(0,'jim7025,1500,4111111111111111')
      throw new Exception('Was excepting it to raise MissingTokensException')
    }
    catch( MissingTokensException ex ) {
      // pass
    }
  }

  void testMissingRef() {
    try {
      parser.parse(0,',1500,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }    
  }

  void testBadAmount() {
    try {
      parser.parse(0,'jim22,xx,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }    
  }

  void testBadCardNbr() {
    try {
      parser.parse(0,'jim22,1500,xxxxxx,10,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }
  }

  void testBadCardExpMth() {
    try {
      parser.parse(0,'jim22,1500,4111111111111111,15,2020,Rick,Hunter,rick@sdf3.com,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }
  }

  void testBadCardExpYear() {
    try {
      parser.parse(0,'jim22,1500,4111111111111111,10,1995,Rick,Hunter,rick@sdf3.com,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }
  }

  void testBadFirstName() {
    try {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,,Hunter,rick@sdf3.com,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }
  }

  void testBadLastName() {
    try {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,Rick,,rick@sdf3.com,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }
  }

  void testBadEmail() {
    try {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,Rick,Hunter,,M5H 2N2')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }
  }

  void testBadZipCode() {
    try {
      parser.parse(0,'jim22,1500,4111111111111111,10,2020,Rick,Hunter,rick@sdf3.com,!!')
      throw new Exception('Was excepting it to raise ValueError')
    }
    catch( IllegalArgumentException ex ) {
      // pass
    }
  }
}