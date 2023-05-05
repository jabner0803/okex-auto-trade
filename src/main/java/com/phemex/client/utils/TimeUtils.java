package com.phemex.client.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 1682993940
 */
public class TimeUtils {

    public static long preTimeSeconds(long inputSeconds, int preMinutes) {
        return Instant.ofEpochSecond(inputSeconds).minus(preMinutes, ChronoUnit.MINUTES).getEpochSecond();
    }

    public static long preTimeMilli(Instant now, int preHours) {
        return now.minus(preHours, ChronoUnit.HOURS).toEpochMilli();
    }
}
