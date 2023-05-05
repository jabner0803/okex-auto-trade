package com.phemex.client.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ConvertorUtils {


    public static BigDecimal convertToRealValue(long scaleValueEv, int scaleRatio) {
        return BigDecimal.valueOf(scaleValueEv).divide(BigDecimal.TEN.pow(scaleRatio), scaleRatio, RoundingMode.DOWN);
    }

    public static BigDecimal convertToScaleValue(String realValue, int scaleRatio) {
        return (new BigDecimal(realValue)).multiply(BigDecimal.TEN.pow(scaleRatio));
    }

    public static boolean isClosePrice(BigDecimal left, BigDecimal right, BigDecimal gap) {
        return left.subtract(right).abs().compareTo(gap) <= 0;
    }
}
