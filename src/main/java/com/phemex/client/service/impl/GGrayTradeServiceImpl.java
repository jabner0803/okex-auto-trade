package com.phemex.client.service.impl;


import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.phemex.client.constant.KlineInterval;
import com.phemex.client.constant.OrdStatus;
import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.Side;
import com.phemex.client.constant.TriggerType;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.market.AccountOrderPositionEvent;
import com.phemex.client.domain.market.OrderBookPEvent;
import com.phemex.client.domain.market.PKlinePushEvent;
import com.phemex.client.service.AbstractGrayTradeService;
import com.phemex.client.service.PhemexWebSocketClient;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.service.quant.KlineBasedQuant;
import com.phemex.client.utils.ConvertorUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.enums.ReadyState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static com.phemex.client.constant.ClientConstants.phemexClientEventBus;

@Slf4j
@Service
@EnableScheduling
@ConditionalOnProperty(value = "phemex.client.trade.usdt", matchIfMissing = true)
public class GGrayTradeServiceImpl extends AbstractGrayTradeService {
    @Autowired
    private PhemexWebSocketClient phemexWebSocketClient;

    @Autowired
    private KlineBasedQuant klineBasedQuant;

    private AtomicBoolean grayUpdateFlag = new AtomicBoolean(false);
    @Autowired
    @Qualifier(value = phemexClientEventBus)
    private EventBus asyncBus;
    private BigDecimal PRICE_MOVEMENT = BigDecimal.valueOf(2L);

    private OrderBookPEvent askOrderBook;
    private OrderBookPEvent bidOrderBook;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void afterSingletonsInstantiated() {
        asyncBus.register(this);
        assert phemexWebSocketClient != null : "PhemexWebSocketClient not initialized yet";
        assert grayProp != null : "GrayProp not initialized yet";
        init();
    }

    private void init() {
        try {
            phemexWebSocketClient.connectBlocking(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("phemexWebSocketClient exception: {}", e.getMessage());
        }
        phemexWebSocketClient.login();
        subscribeData();
//        test();
    }


    private void subscribeData() {
        phemexWebSocketClient.subscribeGOrderBook(grayProp.getContract());
        phemexWebSocketClient.subscribeKline(grayProp.getContract(), KlineInterval.MINUTE_1);
    }

    private void test() {
        List<GNewOrderModelVo> filledOrders = phemexClient.queryFilledGOrders("BTCUSDT", grayProp.getBaseCurrency(),
                        1682669969609L, OrdStatus.Filled.getCode())
                .stream().filter(order -> order.getPosSide().getCode() == order.getSide().getCode()).collect(Collectors.toList());
        assert filledOrders != null : "";
        phemexClient.createReduceOnlyOrder("BTCUSDT", TriggerType.ByMarkPrice, OrdType.LimitIfTouched, Side.Buy, false, "0.001", "28700", "29600", "27000", "Long");

    }

    @Subscribe
    public void orderBooks(OrderBookPEvent bookPEvent) {
        log.debug("Received order book event {}", bookPEvent);
        if (!CollectionUtils.isEmpty(bookPEvent.getOrderbook_p().getAsks())) {
            askOrderBook = bookPEvent;
        } else if (!CollectionUtils.isEmpty(bookPEvent.getOrderbook_p().getBids())) {
            bidOrderBook = bookPEvent;
        }
    }

    @Subscribe
    public void subscribeKlines(PKlinePushEvent pklinePushEvent) throws Exception {
        log.debug("Received klinePushEvent {}", pklinePushEvent);
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (!tradingStrategy.containsKey(pklinePushEvent.getSymbol())) {
                tradingStrategy.put(pklinePushEvent.getSymbol(), super.initTradingStrategy(pklinePushEvent.getKline().get(0).getClose(), grayProp.getStepRatioEv()));
            } else {
                GrayTradingContext context = klineBasedQuant.refreshKlines(tradingStrategy.get(pklinePushEvent.getSymbol()), pklinePushEvent.getKline().get(0));
                if (context != null) {
                    tradingStrategy.put(pklinePushEvent.getSymbol(), context);
                }
            }

        } finally {
            writeLock.unlock();
        }
    }

    @Subscribe
    public void subscribeAop(AccountOrderPositionEvent accountOrderPositionEvent) {
        log.debug("Received subscribeAop {}", accountOrderPositionEvent);
    }
    /**
     * Asks -> Sell, Bids -> Buy
     * TODO 目标价可能不是第一档，需要优化
     *
     * @param context
     * @param askOrderBook
     * @param bidOrderBook
     * @return long
     */
    @VisibleForTesting
    private long triggeredPrice(GrayTradingContext context, OrderBookPEvent askOrderBook, OrderBookPEvent bidOrderBook) {

        if (CollectionUtils.isEmpty(bidOrderBook.getOrderbook_p().getBids()) || CollectionUtils.isEmpty(askOrderBook.getOrderbook_p().getAsks())) {
            return 0L;
        }

        if (!CollectionUtils.isEmpty(askOrderBook.getOrderbook_p().getAsks())) {
            BigDecimal level1AskPrice = new BigDecimal(askOrderBook.getOrderbook_p().getAsks().get(0).getPriceRq());
            BigDecimal minSellPrice = BigDecimal.valueOf(context.getShortPrices()[0], context.getScaleRatio());
            if (level1AskPrice.subtract(minSellPrice).compareTo(PRICE_MOVEMENT) > 0) {
                return context.getShortPrices()[0];
            }
        }

        if (!CollectionUtils.isEmpty(bidOrderBook.getOrderbook_p().getBids())) {
            BigDecimal level1BidPrice = new BigDecimal(bidOrderBook.getOrderbook_p().getBids().get(0).getPriceRq());
            BigDecimal maxLongPrice = ConvertorUtils.convertToRealValue(context.getLongPrices()[0], context.getScaleRatio());
            if (maxLongPrice.subtract(level1BidPrice).compareTo(PRICE_MOVEMENT) > 0) {
                return context.getLongPrices()[0];
            }
        }
        return 0;
    }

    @PreDestroy
    public void destroy() {
        phemexClient.cancelAllGOrders(grayProp.getContract(), false);
    }


    @Scheduled(fixedDelay = 2 * 1000)
    public void scheduleTask() {
        if (askOrderBook != null && bidOrderBook != null) {
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                GrayTradingContext context;
                if (!tradingStrategy.containsKey(askOrderBook.getSymbol())) {
                    context = super.initTradingStrategy(askOrderBook.getOrderbook_p().getAsks().get(0).getPriceRq(), grayProp.getStepRatioEv());
                } else {
                    try {
                        context = strategyService.adjustStrategy(tradingStrategy.get(askOrderBook.getSymbol()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (context != null) {
                    tradingStrategy.put(context.getSymbol(), context);
                }
            } finally {
                writeLock.unlock();
            }

        }
    }

    @Scheduled(fixedDelay = 15 * 1000)
    public void sendHeartbeat() {
        if (phemexWebSocketClient.isFlushAndClose()) {
            log.debug("Detected web socket connect is closed, try re-connect now");
            this.reconnect();
            subscribeData();
        } else {
            log.debug("Web socket connected");
            try {
                this.phemexWebSocketClient.sendHeartBeat();
            }catch (Exception ex) {
                log.debug("Detected web socket connect is closed, try re-connect now");
                reconnect();
                subscribeData();
            }

        }
    }

    private void reconnect() {
        if (this.phemexWebSocketClient == null) {
            return;
        }
        if (!this.phemexWebSocketClient.isOpen()) {
            if (this.phemexWebSocketClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    this.phemexWebSocketClient.connect();
                } catch (IllegalStateException ex) {
                    log.error("reconnect exception", ex);
                }
            }else if (this.phemexWebSocketClient.getReadyState().equals(ReadyState.CLOSING) || this.phemexWebSocketClient.getReadyState().equals(ReadyState.CLOSED)){
                this.phemexWebSocketClient.reconnect();
            }
        }
    }

}
