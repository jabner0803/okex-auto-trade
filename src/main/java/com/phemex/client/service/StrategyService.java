package com.phemex.client.service;

import com.phemex.client.domain.market.PKlinePushEvent;
import com.phemex.client.service.ctx.GrayTradingContext;

public interface StrategyService <T>{

    T createStrategy(String priceRp, long stepRatioEv);

    T adjustStrategy(T cxt) throws Exception ;

    GrayTradingContext quickMoveStrategy(T ctx, PKlinePushEvent.PKlineEntry klineEntry, int moveDirection, int fluctuateRatio) throws Exception;

    GrayTradingContext quitQuickMoveStrategy(T ctx, PKlinePushEvent.PKlineEntry klineEntry) throws Exception;

    void shutdownStrategy(T cxt);
}
