package com.phemex.client.utils;

import com.phemex.client.constant.Side;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {

    public static BigDecimal PRICE_MOVEMENT = BigDecimal.valueOf(2L);

    public static long findClosePrice(String priceRv, Long[] prices, Integer scaleRatio) {
        BigDecimal sourcePrice = new BigDecimal(priceRv);
        for (Long price : prices) {
            if (ConvertorUtils.isClosePrice(sourcePrice, ConvertorUtils.convertToRealValue(price, scaleRatio ), PRICE_MOVEMENT)) {
                return price;
            }
        }
        return 0L;
    }

    /**
     *      * 上涨，卖空 -> 平空， 平多 -> Do nothing
     *      * 下跌，买多 -> 平多， 平空 -> Do nothing
     *      * Long = 1, Short = 2
     *      * Buy = 1, Sell = 2
     * @param order
     * @param scaleRatio
     * @param stepRatioEv
     * @return
     */
    public static long hedgePriceEv(String priceRp, Side side, int scaleRatio, long stepRatioEv) {
        BigDecimal basePriceEv = ConvertorUtils.convertToScaleValue(priceRp, scaleRatio);
        return calculatePrice(basePriceEv.longValue(), stepRatioEv, side == Side.Buy ? 1 : -1, scaleRatio);
    }


    public static long calculatePrice(long basePrice, long stepRatioEv, int direction, int scaleRatio) {
        BigDecimal targetPrice = BigDecimal.valueOf(basePrice)
                .add(BigDecimal.valueOf(basePrice).multiply(BigDecimal.valueOf(stepRatioEv)).multiply(BigDecimal.valueOf(direction))
                        .divide(BigDecimal.TEN.pow(scaleRatio), scaleRatio, RoundingMode.DOWN));
        return targetPrice.longValue();
    }

    public static Long[] buildOpenPriceArray(int steps, long openPrice, int direction, long stepRatioEv, int scaleRatio) {
        Long[] priceArr = new Long[steps];
        for (int i =0;i<steps;i++) {
            priceArr[i] = calculatePrice(i == 0 ? openPrice : priceArr[i-1], stepRatioEv, direction, scaleRatio);
        }
        return priceArr;
    }

    public static long stepRatioStrategy(long originStepRatioEv, int scaleRatio, int fluctuateRatio) {
        return BigDecimal.valueOf(originStepRatioEv).multiply(BigDecimal.valueOf(1).add(BigDecimal.valueOf(fluctuateRatio).divide(BigDecimal.valueOf(2), scaleRatio, RoundingMode.DOWN))).longValue();
    }

}
