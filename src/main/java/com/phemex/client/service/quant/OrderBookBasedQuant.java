package com.phemex.client.service.quant;

import com.phemex.client.domain.market.OrderBookPEvent;
import com.phemex.client.service.ctx.GrayTradingContext;
import org.springframework.beans.factory.SmartInitializingSingleton;

public interface OrderBookBasedQuant extends SmartInitializingSingleton {

    void refresh(GrayTradingContext context, OrderBookPEvent bookPEvent);
}
