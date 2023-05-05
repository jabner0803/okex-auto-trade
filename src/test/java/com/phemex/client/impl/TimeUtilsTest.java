package com.phemex.client.impl;

import com.phemex.client.utils.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class TimeUtilsTest {

    @Test
    void testTime() {
        long input = 1682993940;
        long output = TimeUtils.preTimeSeconds(input, 1);
        Assertions.assertEquals(1682993880, output);
    }

    @Test
    void test() {
        long milliPre24H = TimeUtils.preTimeMilli(Instant.now(), 24);
        Assertions.assertNotNull(milliPre24H);
    }
}
