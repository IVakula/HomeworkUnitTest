package homework21;


import org.junit.jupiter.api.Test;

public class SimpleMathLibraryTest {
    @Test
    public void testAdd() {
        double a = 10;
        double b = 12;
        SimpleMathLibrary simpleMathLibrary = new SimpleMathLibrary();
        if (simpleMathLibrary.add(a, b) == 22) {
            System.out.println("OK");
        } else {
            System.out.println("NOK");
        }
    }

    @Test
    public void testMinus() {
        double a = 25;
        double b = 12;
        SimpleMathLibrary simpleMathLibrary = new SimpleMathLibrary();
        if (simpleMathLibrary.minus(a, b) == 13) {
            System.out.println("OK");
        } else {
            System.out.println("NOK");
        }
    }
}
