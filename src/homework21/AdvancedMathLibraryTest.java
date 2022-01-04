package homework21;

import org.junit.jupiter.api.Test;

public class AdvancedMathLibraryTest {
    @Test
    public void testMultiply() {
        double a = 10;
        double b = 12;
        AdvancedMathLibrary advancedMathLibrary = new AdvancedMathLibrary();
        if (advancedMathLibrary.multiply(a, b) == 120) {
            System.out.println("OK");
        } else {
            System.out.println("NOK");
        }
    }

    @Test
    public void testSquare() {
        double a = 10;
        AdvancedMathLibrary advancedMathLibrary = new AdvancedMathLibrary();
        if (advancedMathLibrary.square(a) == 100) {
            System.out.println("OK");
        } else {
            System.out.println("NOK");
        }
    }
}
