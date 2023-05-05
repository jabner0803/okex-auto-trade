package com.phemex.client.constant;

import com.phemex.client.domain.market.AccountOrderPositionEvent;
import com.phemex.client.domain.market.GPositionInfoEvent;
import com.phemex.client.domain.market.OrderBookPEvent;
import com.phemex.client.domain.market.OrderbookEvent;
import com.phemex.client.domain.market.PKlinePushEvent;
import com.phemex.client.domain.market.SymbolEvent;
import com.phemex.client.domain.market.TickPushEvent;
import com.phemex.client.domain.market.TradePushEvent;
import com.phemex.client.domain.market.response.ExchangeReplyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum PhemexServerEventType {

    REPLY("", ExchangeReplyResponse.class, "", "", false),

    TICK_EVENT("tick", TickPushEvent.class, "tick.subscribe", "tick.unsubscribe", false),

    TRADE_EVENT("trades", TradePushEvent.class, "trade.subscribe", "trade.unsubscribe", true),

    ORDER_BOOK_EVENT("book", OrderbookEvent.class, "orderbook.subscribe", "orderbook.unsubscribe", true),

    ORDER_BOOK_P_EVENT("orderbook_p", OrderBookPEvent.class, "orderbook_p.subscribe", "orderbook_p.unsubscribe", true),

    KLINE_EVENT("kline_p", PKlinePushEvent.class, "kline_p.subscribe", "kline_p.unsubscribe", true) {
        @Override
        public String extractKey(SymbolEvent symbolEvent) {
            PKlinePushEvent pklinePushEvent = (PKlinePushEvent) symbolEvent;
            if (!pklinePushEvent.getKline().isEmpty()) {
                return this.name() + "_" + pklinePushEvent.getSymbol() + "_" + pklinePushEvent.getKline().get(0).getInterval();
            }
            return "";
        }
    },

    ACCOUNT_ORDER_EVENT("index_market24h", AccountOrderPositionEvent.class, "aop_p.subscribe", "aop_p.unsubscribe", true),

    ACCOUNT_POSITION_INFO("position_info", GPositionInfoEvent.class, "aop_p.subscribe", "aop_p.unsubscribe", true);

    private String tag;

    private Class<?> clazz;

    private String subOp;

    private String unSubOp;

    private boolean includeRoot;

    static public PhemexServerEventType toPushType(String tag) {
        return Arrays.stream(PhemexServerEventType.values())
            .filter(x -> x.tag.equalsIgnoreCase(tag))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Failed to find proper push event type for " + tag));
    }

    static public PhemexServerEventType toPushType(Function<String, Boolean> keyFieldChecker) {
        return Arrays.stream(PhemexServerEventType.values())
            .filter(x -> keyFieldChecker.apply(x.getTag()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Failed to find proper push event type "));
    }

    public String toKey(String arg) {
        return this.name() + "_" + arg;
    }

    public String extractKey(SymbolEvent args) {
        return this.name() + "_" + args.getSymbol();
    }

}
