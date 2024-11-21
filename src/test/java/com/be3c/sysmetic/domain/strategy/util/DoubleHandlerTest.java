package com.be3c.sysmetic.domain.strategy.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class DoubleHandlerTest {
    private DoubleHandler doubleHandler;

    @BeforeEach
    void setUp() {
        doubleHandler = new DoubleHandler();
    }

    @Test
    void cutDouble_ShouldCutToFourDecimalPlaces() {
        // given
        Double input1 = 123.456789;
        Double input2 = 123.4567;

        // when
        Double result1 = doubleHandler.cutDouble(input1);
        Double result2 = doubleHandler.cutDouble(input2);

        // then
        assertThat(result1).isEqualTo(123.4567);
        assertThat(result2).isEqualTo(123.4567);
    }

    @Test
    void compare_ShouldReturnTrue_WhenValuesAreEqualWithinEpsilon() {
        // given
        Double value1 = 123.4567;
        Double value2 = 123.45675;

        // when
        boolean result = doubleHandler.compare(value1, value2);

        // then
        assertTrue(result);
    }

    @Test
    void compare_ShouldReturnFalse_WhenValuesAreOutsideEpsilon() {
        // given
        Double value1 = 123.4567;
        Double value2 = 123.4568;

        // when
        boolean result = doubleHandler.compare(value1, value2);

        // then
        assertFalse(result);
    }

    @Test
    void compare_ShouldReturnFalse_WhenValuesAreSignificantlyDifferent() {
        // given
        Double value1 = 123.4567;
        Double value2 = 124.4567;

        // when
        boolean result = doubleHandler.compare(value1, value2);

        // then
        assertFalse(result);
    }
}
