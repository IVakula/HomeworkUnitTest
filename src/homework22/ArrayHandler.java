package homework22;

import java.util.Arrays;


public class ArrayHandler {
    public static int[] processing(int[] array) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 4) {
                index = i;
            }
        }
        if (index < 0) {
            throw new RuntimeException();
        }
        return Arrays.copyOfRange(array, index + 1, array.length);
    }

    public static boolean checkArray(int[] array) {
        boolean containsOne = false;
        boolean containsFour = false;
        for (int j : array) {
            if (j == 1) {
                containsOne = true;
            } else if (j == 4) {
                containsFour = true;
            } else {
                return false;
            }
        }
        return containsFour && containsOne;
    }
}
