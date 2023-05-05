package com.phemex.client.service.impl;

import com.phemex.client.constant.OrdStatus;
import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.Side;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.domain.market.PKlinePushEvent;
import com.phemex.client.prop.GrayProp;
import com.phemex.client.service.OrderService;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.service.PositionService;
import com.phemex.client.service.StrategyService;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.utils.ConvertorUtils;
import com.phemex.client.utils.PriceUtils;
import com.phemex.client.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyServiceImpl implements StrategyService<GrayTradingContext> {

    private final GrayProp grayProp;

    private final PhemexClient phemexClient;

    private final OrderService orderService;

    private final PositionService positionService;

    @Override
    public GrayTradingContext createStrategy(String priceRp, long stepRatioEv) {
        long triggeredPrice = ConvertorUtils.convertToScaleValue(priceRp, grayProp.getScaleRatio()).longValue();
        return init(triggeredPrice, stepRatioEv, ctx -> {
            while(true) {
                List<GOrderModelVo> activeOrders = phemexClient.queryAllActiveGOrders(ctx.getSymbol());
                if (!CollectionUtils.isEmpty(activeOrders) && activeOrders.size() >= grayProp.getSteps() * 2) {
                    log.info("create strategy succeed, init orders: {}", activeOrders);
                    ctx.setActiveOrders(activeOrders);
                    return;
                }
            }
        });
    }

    /**
     * 价格下移，买单被吃掉;1) 添加卖单 2) 添加平多的卖多单 3) 增加远端买单 4) 移除远端卖单
     * 价格上涨，卖单被吃掉;1) 添加买单 2) 添加平空的买空单 3) 添加远端卖单 5) 移除远端买单
     * 查询到新的订单成交 -> 更新GrayTradingContext, 以最新成交的价格为基准价格调整网格
     * 注意：查询的成交订单是按照创建时间查询和排序返回的，这里为了避免遗漏，做了特殊处理；1) 24小时过期的订单直接删除，2) 查询过去24小时成交的订单，记录订单的成交时间进行筛选
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    public GrayTradingContext adjustStrategy(GrayTradingContext ctx) throws Exception {

        StopWatch sp = new StopWatch();
        sp.start();

        long milli24HBefore = TimeUtils.preTimeMilli(Instant.now(), 24);
        // clear 24hour overdue orders
        List<GOrderModelVo> activeOrders = orderService.searchAndDropOverdueOrders(ctx.getSymbol(), milli24HBefore * 1000_000L);
        if (CollectionUtils.isEmpty(activeOrders)) {
            log.error("cannot find any activeOrders");
        }
        // process filled orders
        List<GNewOrderModelVo> filledOrders = orderService.queryFilledOrders(ctx.getSymbol(), grayProp.getBaseCurrency(), OrdStatus.Filled.getCode(), milli24HBefore, ctx.getLatestFilledOrderTimeMills());

        if (CollectionUtils.isEmpty(filledOrders)) {
            sp.stop();
            log.debug("adjustStrategy skip as filled orders is empty: {}, took : {} ms", ctx, sp.getTotalTimeMillis());
            return null;
        }
        ctx.setLatestFilledOrderTimeMills(filledOrders.get(0).getUpdatedAt());
        closeFilledOrders(ctx, filledOrders, OrdType.Limit);

        long newOpenPriceEv = openPriceBasedOnFilledOrder(ctx, filledOrders);

        if (newOpenPriceEv == 0L) {
            log.info("Latest filled orders are manually created");
            return ctx;
        }
        GrayTradingContext ctx_1 = rebuildGTC(ctx, newOpenPriceEv);

        // refresh orders
        process(ctx_1, activeOrders,  data -> {
            // zero snapshot prices
            if (data.containsKey(0L)) {
                data.get(0L).stream().filter(order -> isInValidPrice(ctx_1, order.getPriceRp()))
                        .forEach(order -> {
                            log.info("adjustStrategy try to delete over price limit order: {}", order);
                            phemexClient.cancelGOrder(order.getOrderID(), order.getSymbol(), order.getSide() == Side.Sell ? "Short" : "Long");
                        });
            }

            data.keySet().stream().filter(price -> price != 0L).forEach(price -> {
                List<GOrderModelVo> selectedOrders = data.get(price);
                // empty prices
                if (CollectionUtils.isEmpty(selectedOrders)) {
                    Side side = price > newOpenPriceEv ? Side.Sell : Side.Buy;
                    String posSide = side == Side.Buy ? "Long" : "Short";
                    log.info("adjustStrategy try create order with price: {}, side: {}, posSide: {}", price, side, posSide);
                    orderService.checkAndCreateOrder(ctx_1, price, ctx_1.getOrderQtyEv(), side, posSide, OrdType.Limit, false);
                } else if (selectedOrders.size() > 1) { // duplicate orders
                    selectedOrders.subList(1, selectedOrders.size()).forEach(order -> {
                        log.info("adjustStrategy try delete duplicate order: {}", order);
                        phemexClient.cancelGOrder(order.getOrderID(), order.getSymbol(), order.getSide() == Side.Sell ? "Short" : "Long");
                    });
                }
            });
        });
        sp.stop();
        log.info("adjustStrategy : {} completed, build grayTradingContext: {} took: {} Millis", ctx, ctx_1, sp.getTotalTimeMillis());
        return ctx_1;
    }

    private long openPriceBasedOnFilledOrder(GrayTradingContext ctx, List<GNewOrderModelVo> filledOrders) {
        if (CollectionUtils.isEmpty(filledOrders)) {
            return 0L;
        }
        long newOpenPriceEv = PriceUtils.findClosePrice(filledOrders.get(0).getPriceRp(), filledOrders.get(0).getSide() == Side.Buy ? ctx.getLongPrices() : ctx.getShortPrices(), ctx.getScaleRatio());
        log.info("adjustStrategy starting to process newOpenPriceEv: {}", newOpenPriceEv);
        return newOpenPriceEv;
    }

    private GrayTradingContext rebuildGTC(GrayTradingContext ctx, long newOpenPriceEv) {
        GrayTradingContext ctx_1 = init(ctx.getSymbol(), newOpenPriceEv, ctx.getOrderQtyEv(), ctx.getStepRatioEv(),
                ctx.getSteps(), ctx.getRiskThresholdRatioEv(), ctx.getScaleRatio());
        ctx_1.setVersion(ctx_1.getVersion() + 1);
        ctx_1.setLatestFilledOrderTimeMills(ctx.getLatestFilledOrderTimeMills());
        AtomicLong createTimeAt = new AtomicLong(ctx_1.getLatestFilledOrderTimeMills());
        // 查找的订单 start >= createdAt, 所以这里需要加一，避免重复
        ctx_1.setLatestFilledOrderTimeMills(createTimeAt.get() + 1L);
        return ctx_1;
    }

    private void closeFilledOrders(GrayTradingContext ctx, List<GNewOrderModelVo> filledOrders, OrdType ordType) {
        filledOrders.forEach(order -> {
            // build close position order
            log.info("closeFilledOrders create close order for order: {}", order);
            /**
             * Attention here we still user grayProperty.stepRatioEV
             */
            try {
                orderService.checkAndCreateOrder(ctx, PriceUtils.hedgePriceEv(order.getExecPriceRp(), order.getSide(), ctx.getScaleRatio(), grayProp.getStepRatioEv()), ctx.getOrderQtyEv(),
                        order.getSide() == Side.Buy ? Side.Sell : Side.Buy, order.getPosSide().name(), ordType, true);
            } catch (Exception ex) {
                log.error("closeFilledOrders got exception for close order: {}", order);
            }
        });
    }

    /**
     * 针对价格的急速波动，本质上应该是顺势而为
     * 1) 取消所有价格区间设置的条件单
     * 2) 调整价格区间，重建条件单
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    public GrayTradingContext quickMoveStrategy(GrayTradingContext ctx, PKlinePushEvent.PKlineEntry klineEntry, int moveDirection, int fluctuateRatio) throws Exception {

        if (!ctx.isEnabled() || fluctuateRatio < ctx.getFluctuateRatio()) {
            return null;
        }
        StopWatch sp = new StopWatch();
        sp.start();
        log.info("quickMoveStrategy starting, moveDirection: {}, stepRatioEv: {}", moveDirection, PriceUtils.stepRatioStrategy(grayProp.getStepRatioEv(), grayProp.getScaleRatio(), fluctuateRatio));


        long milli24HBefore = TimeUtils.preTimeMilli(Instant.now(), 24);
        // clear 24hour overdue orders
        List<GOrderModelVo> activeOrders = orderService.searchAndDropOverdueOrders(ctx.getSymbol(), milli24HBefore * 1000_000L);
        // close orders
        process(ctx, activeOrders, data -> data.values().stream().flatMap(Collection::stream).forEach(orderService::cancelOrder));
        // TODO close opposite position
//        positionService.closePosition(ctx, moveDirection);
        // init GrayTradingContext
        GrayTradingContext newCtx = createStrategy(klineEntry.getClose(), PriceUtils.stepRatioStrategy(grayProp.getStepRatioEv(), grayProp.getScaleRatio(), fluctuateRatio));
        newCtx.setFluctuateRatio(fluctuateRatio);
        newCtx.setEnabled(false);
        sp.stop();
        log.info("Took {} ms, quickMoveStrategy finish, GrayTradingContext: {}", sp.getTotalTimeMillis(), newCtx);
        return newCtx;
    }

    /**
     * 结束活动订单
     * @param ctx
     * @return
     * @throws Exception
     */
    @Override
    public GrayTradingContext quitQuickMoveStrategy(GrayTradingContext ctx, PKlinePushEvent.PKlineEntry klineEntry) throws Exception {
        if (ctx.isEnabled()) {
            log.debug("Skip quitQuickMoveStrategy : {}", ctx);
            return null;
        }
        StopWatch sp = new StopWatch();
        sp.start();
        long milli24HBefore = TimeUtils.preTimeMilli(Instant.now(), 24);
        // clear 24hour overdue orders
        List<GOrderModelVo> activeOrders = orderService.searchAndDropOverdueOrders(ctx.getSymbol(), milli24HBefore * 1000_000L);
        // 关闭所有条件单
        process(ctx, activeOrders, data -> data.values().stream().flatMap(Collection::stream).forEach(orderService::cancelOrder));
        // 创建初始订单
        GrayTradingContext newCtx = createStrategy(klineEntry.getClose(), grayProp.getStepRatioEv());
        sp.stop();
        log.info("quitQuickMoveStrategy take effective, took {} ms", sp.getTotalTimeMillis());
        return newCtx;
    }

    private void process(GrayTradingContext context, List<GOrderModelVo> activeOrders, Consumer<Map<Long, List<GOrderModelVo>>> consumer) {
        log.info("Starting to refresh context: {}", context);

        if (CollectionUtils.isEmpty(activeOrders)) {
            log.info("No active orders found for symbol: {}", context.getSymbol());
            return;
        }
        Map<Long, List<GOrderModelVo>> orderMap = new HashMap<>();
        Arrays.stream(context.getLongPrices()).forEach(priceEv -> orderMap.put(priceEv, new ArrayList<>()));
        Arrays.stream(context.getShortPrices()).forEach(priceEv -> orderMap.put(priceEv, new ArrayList<>()));
        activeOrders.forEach(order -> {
                    long targetPriceEv = PriceUtils.findClosePrice(order.getPriceRp(), order.getSide() == Side.Buy ? context.getLongPrices() : context.getShortPrices(), context.getScaleRatio());
                    List<GOrderModelVo> selectedOrders = orderMap.containsKey(targetPriceEv) ? orderMap.get(targetPriceEv) : new ArrayList<>();
                    selectedOrders.add(order);
                    orderMap.put(targetPriceEv, selectedOrders);
                });
        consumer.accept(orderMap);
    }

    private boolean isInValidPrice(GrayTradingContext context, String priceRq) {
        BigDecimal price = ConvertorUtils.convertToScaleValue(priceRq, context.getScaleRatio());
        return (price.compareTo(BigDecimal.valueOf(context.getLowThresholdPrice())) < 0
                || price.compareTo(BigDecimal.valueOf(context.getLongPrices()[context.getLongPrices().length - 1])) < 0
                || price.compareTo(BigDecimal.valueOf(context.getUpperThresholdPrice())) > 0
                || price.compareTo(BigDecimal.valueOf(context.getShortPrices()[context.getShortPrices().length - 1])) > 0);

    }

    @Override
    public void shutdownStrategy(GrayTradingContext cxt) {

    }

    private GrayTradingContext init(long initPriceEv, long stepRatioEv,Consumer<GrayTradingContext> consumer) {
        GrayTradingContext ctx = init(grayProp.getContract(), initPriceEv, grayProp.getOrderQty(), stepRatioEv, grayProp.getSteps(), grayProp.getRiskThresholdRatioEv(), grayProp.getScaleRatio());
        Arrays.stream(ctx.getLongPrices()).forEach(priceEv -> orderService.checkAndCreateOrder(ctx, priceEv, ctx.getOrderQtyEv(), Side.Buy, "Long", OrdType.Limit, false));
        Arrays.stream(ctx.getShortPrices()).forEach(priceEv -> orderService.checkAndCreateOrder(ctx, priceEv, ctx.getOrderQtyEv(), Side.Sell, "Short", OrdType.Limit, false));
        consumer.accept(ctx);
        return ctx;
    }

    private GrayTradingContext init(String symbol, long priceEv, long orderQty, long stepRatioEv, int steps,
                                                long riskThresholdRationEv, int scaleRatio) {
        return GrayTradingContext.builder()
                .startTime(Timestamp.from(Instant.now()).getTime())
                .openPrice(priceEv)
                .stepRatioEv(stepRatioEv)
                .symbol(symbol)
                .orderQtyEv(orderQty)
                .steps(steps)
                .riskThresholdRatioEv(riskThresholdRationEv)
                .shortPrices(PriceUtils.buildOpenPriceArray(steps, priceEv, 1, stepRatioEv, scaleRatio))
                .longPrices(PriceUtils.buildOpenPriceArray(steps, priceEv, -1, stepRatioEv, scaleRatio))
                .lowThresholdPrice(BigDecimal.valueOf(priceEv).subtract(BigDecimal.valueOf(priceEv).multiply(ConvertorUtils.convertToRealValue(riskThresholdRationEv, scaleRatio))).longValue())
                .upperThresholdPrice(BigDecimal.valueOf(priceEv).add(BigDecimal.valueOf(priceEv).multiply(ConvertorUtils.convertToRealValue(riskThresholdRationEv, scaleRatio))).longValue())
                .build();
    }


}
