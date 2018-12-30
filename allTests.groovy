import groovy.util.GroovyTestSuite
import junit.framework.Test
import junit.textui.TestRunner

// executes all test classes in a folder
class AllTests { 
   static Test suite() {
      def suite = new GroovyTestSuite()
      
      new File("test").eachFileMatch(~/.*\.groovy/) { file ->
         suite.addTestSuite(suite.compile(file.absolutePath))
      }

      return suite 
   } 
} 

TestRunner.run(AllTests.suite())