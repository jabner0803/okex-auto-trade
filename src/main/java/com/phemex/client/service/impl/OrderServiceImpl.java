package com.phemex.client.service.impl;

import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.Side;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.service.OrderService;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.utils.ConvertorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final PhemexClient phemexClient;

    @Override
    public void checkAndCreateOrder(GrayTradingContext context, long price, long orderQty, Side side, String posSide, OrdType ordType, boolean closeOnTrigger) {
        if (ordType != OrdType.Market && (price <= context.getLowThresholdPrice() || price >= context.getUpperThresholdPrice())) {
            return;
        }
        String orderQtyRv = ConvertorUtils.convertToRealValue(orderQty, context.getScaleRatio()).toString();
        String priceRv = ConvertorUtils.convertToRealValue(price, context.getScaleRatio()).toString();
        phemexClient.createGContractOrder(context.getSymbol(), orderQtyRv, priceRv, side, ordType, posSide, closeOnTrigger);
    }

    @Override
    public List<GOrderModelVo> searchAndDropOverdueOrders(String symbol, long startTimeNs) {

        List<GOrderModelVo> activeOrders = queryAllActiveOrders(symbol);
        if (CollectionUtils.isEmpty(activeOrders)) {
            return new ArrayList<>();
        }

        activeOrders.stream().filter(order -> order.getActionTimeNs() <= startTimeNs)
                .forEach(this::cancelOrder);
        return activeOrders.stream().filter(order -> order.getActionTimeNs() > startTimeNs)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(GOrderModelVo order) {
        log.info("Starting to cancel order: {}", order);
        phemexClient.cancelGOrder(order.getOrderID(), order.getSymbol(), order.getSide() == Side.Sell ? "Short" : "Long");
    }

    @Override
    public List<GOrderModelVo> queryAllActiveOrders(String symbol) {
        return phemexClient.queryAllActiveGOrders(symbol)
                .stream()
                .filter(order -> order.getOrderType() == OrdType.Limit)
                .filter(order -> order.getExecInst().equalsIgnoreCase("None")).collect(Collectors.toList());
    }

    @Override
    public List<GNewOrderModelVo> queryFilledOrders(String symbol, String baseCurrency, int ordStatus, long startTimeMills, long latestUpdateTimeMills) {
//        long milliPre24h = Instant.now().toEpochMilli();
        List<GNewOrderModelVo> filledOrdersIn24H = phemexClient.queryFilledGOrders(symbol, baseCurrency, startTimeMills, ordStatus)
                .stream()
                .filter(order -> order.getPosSide().getCode() == order.getSide().getCode())
                .filter(order -> order.getUpdatedAt() > latestUpdateTimeMills)
                .sorted(Comparator.comparing(GNewOrderModelVo::getUpdatedAt).reversed())
                .collect(Collectors.toList());
        return filledOrdersIn24H;
    }
}
