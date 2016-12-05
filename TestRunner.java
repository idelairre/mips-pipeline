import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

class TestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(TestSuite.class);
    for (Failure failure : result.getFailures()) {
       System.out.println("Failed: " + failure.toString());
    }
    System.out.println("Passed? " + result.wasSuccessful());
  }
}
