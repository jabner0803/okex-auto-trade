package com.phemex.client.impl;

import com.phemex.client.utils.PriceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PriceUtilsTest {

    @Test
    void testStepRatioStrategy() {
        long result = PriceUtils.stepRatioStrategy(100000L, 8, 3);
        Assertions.assertEquals(250000L, result);
    }
}
