package homework21;

import java.util.List;

public class ProjectTestRunner {
    public static void main(String[] args) {
        System.out.println("****** Run test by class name (String) ***** ");
        new TestRunner().runTestByClassName("homework21.SimpleMathLibraryTest");

        System.out.println("****** Run test by class name (class) ***** ");
        new TestRunner().runTestByClassName(SimpleMathLibraryTest.class);

        System.out.println("****** Run test by class names (String) ***** ");
        new TestRunner().runTestByClassNames("homework21.SimpleMathLibraryTest", "homework21.AdvancedMathLibraryTest");

        System.out.println("****** Run test by class names (class) ***** ");
        new TestRunner().runTestByClassNames(SimpleMathLibraryTest.class, AdvancedMathLibraryTest.class);

        System.out.println("****** Run test by package name ***** ");
        new TestRunner().runTestByPackageName("homework21");

        System.out.println(TestResultParser.parse("test.log"));
    }

}
