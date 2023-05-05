package com.phemex.client.service.quant;

import com.phemex.client.domain.market.PKlinePushEvent;
import com.phemex.client.service.ctx.GrayTradingContext;
import org.springframework.beans.factory.SmartInitializingSingleton;

public interface KlineBasedQuant extends SmartInitializingSingleton {

    GrayTradingContext refreshKlines(GrayTradingContext gtc, PKlinePushEvent.PKlineEntry klineEntry) throws Exception;
}
