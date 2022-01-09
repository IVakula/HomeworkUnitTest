package homework22;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.Arrays;
import java.util.stream.Stream;

public class ArrayHandlerTest {

    @ParameterizedTest
    @MethodSource("validArraysForArrayHandlerProcessingProvider")
    void shouldProcess_whenValidData(int[] expected, int[] data) {
        Assertions.assertArrayEquals(
                expected,
                ArrayHandler.processing(data),
                Arrays.toString(data) + "->" + Arrays.toString(expected)
        );
    }

    private static Stream<Arguments> validArraysForArrayHandlerProcessingProvider() {
        return Stream.of(
                Arguments.of(new int[]{1}, new int[]{1, 4, 1, 4, 5, 4, 1}),
                Arguments.of(new int[]{5, 3, 1}, new int[]{1, 4, 1, 4, 5, 3, 1}),
                Arguments.of(new int[]{}, new int[]{1, 4, 1, 4, 5, 4})
        );
    }


    @Test
    void shouldProcess_throwRuntimeException_whenArrayWithout4() {
        Assertions.assertThrows(
                RuntimeException.class,
                () -> ArrayHandler.processing(new int[]{2, 5, 7})
        );
    }

    @ParameterizedTest
    @MethodSource("validArraysForArrayHandlerCheckArrayProvider")
    void shouldCheckArray_whenValidData(boolean expected, int[] data) {
        Assertions.assertEquals(
                expected,
                ArrayHandler.checkArray(data),
                Arrays.toString(data) + "->" + expected
        );
    }

    private static Stream<Arguments> validArraysForArrayHandlerCheckArrayProvider() {
        return Stream.of(
                Arguments.of(true, new int[]{1, 4, 1, 4, 4, 4, 1}),
                Arguments.of(false, new int[]{1, 1, 1, 1, 1}),
                Arguments.of(false, new int[]{4, 4, 4, 4, 4}),
                Arguments.of(false, new int[]{4, 2, 4, 7, 4})
        );
    }

}
