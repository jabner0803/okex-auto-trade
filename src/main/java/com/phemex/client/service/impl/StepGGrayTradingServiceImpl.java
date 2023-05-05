package com.phemex.client.service.impl;

import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.Side;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.prop.GrayProp;
import com.phemex.client.service.AbstractGrayTradeService;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.utils.ConvertorUtils;
import com.phemex.client.utils.PriceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "phemex.client.trade.usdt.step", matchIfMissing = false)
public class StepGGrayTradingServiceImpl extends AbstractGrayTradeService {

    private final PhemexClient phemexClient;

    private final GrayProp grayProp;

    /**
     * @param context
     */
    public void createHedgeOrders(GrayTradingContext context) {
        // load executed orders
        List<GNewOrderModelVo> filledOrders = phemexClient.queryFilledGOrders(context.getSymbol(), grayProp.getBaseCurrency(), context.getLatestFilledOrderTimeMills(), 7);
        if (CollectionUtils.isEmpty(filledOrders)) {
            return;
        }
        AtomicLong updateTime = new AtomicLong(context.getLatestFilledOrderTimeMills());
        filledOrders.stream().filter(order -> order.getPosSide().getCode() == order.getSide().getCode())
                .forEach(order -> {
            phemexClient.createGContractOrder(order.getSymbol(), order.getOrderQtyRq(), hedgePriceRv(context, order),
                    order.getSide() == Side.Buy ? Side.Sell : Side.Buy, OrdType.Limit, order.getPosSide().name(), true);
            if (updateTime.get() < order.getCreatedAt()) {
                updateTime.set(order.getCreatedAt());
            }
        });
    }

    /**
     * 上涨，卖空 -> 平空， 平多 -> Do nothing
     * 下跌，买多 -> 平多， 平空 -> Do nothing
     * Long = 1, Short = 2
     * Buy = 1, Sell = 2
     * @param context
     * @param order
     * @return
     */
    private String hedgePriceRv(GrayTradingContext context, GNewOrderModelVo order) {
        BigDecimal basePriceEv = ConvertorUtils.convertToScaleValue(order.getExecPriceRp(), context.getScaleRatio());
        long targetPriceEv = PriceUtils.calculatePrice(basePriceEv.longValue(), context.getStepRatioEv(), order.getSide() == Side.Buy ? 1 : -1, context.getScaleRatio());
        return ConvertorUtils.convertToRealValue(targetPriceEv, context.getScaleRatio()).toPlainString();
    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}
