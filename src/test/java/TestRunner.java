import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {

    //Note that mocking JOptionPane is not allowed since it is a static class.
    //Therefore, when running the following test suite you need to press the button
    //"Jag har gjort aktiviteten!" when prompted.

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestFA5.class, TestFK1.class, TestFP11.class,
                TestFP21.class, TestFP22.class, TestFÖ1.class, TestFÖ3.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        if (result.wasSuccessful()) {
            System.out.println("All tests passed successfully.");
        } else {
            System.out.println("Tests above failed.");
        }
        System.exit(0);
    }
}
