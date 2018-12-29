import groovy.util.GroovyTestSuite
import junit.framework.Test
import junit.textui.TestRunner

import test.LineParserTest
import test.CardRequestTest
import test.CardResponseTest

// executes all test classes in the suite
class AllTests { 
   static Test suite() { 
      def allTests = new GroovyTestSuite() 
      allTests.addTestSuite(LineParserTest.class)
      allTests.addTestSuite(CardRequestTest.class)
      allTests.addTestSuite(CardResponseTest.class)
      return allTests 
   } 
} 

TestRunner.run(AllTests.suite())