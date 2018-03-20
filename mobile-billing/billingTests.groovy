// @author Dimitrios Gianninas
// runs all the unit tests for this module

import groovy.util.GroovyTestSuite 
import junit.framework.Test 
import junit.textui.TestRunner 
import test.RecordValidatorTest

class AllTests { 
   static Test suite() { 
      def allTests = new GroovyTestSuite() 
      allTests.addTestSuite(RecordValidatorTest.class) 
      return allTests 
   } 
} 

TestRunner.run(AllTests.suite())