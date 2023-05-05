package com.phemex.client.impl;

import com.phemex.client.listener.PhemexMessageListener;
import com.phemex.client.service.PhemexWebSocketClient;
import com.phemex.client.service.PositionService;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.service.impl.GGrayTradeServiceImpl;
import com.phemex.client.service.impl.StrategyServiceImpl;
import com.phemex.client.utils.PriceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class GrayTradeUSDTServiceTest extends BaseTest{

    @MockBean
    private PhemexMessageListener phemexMessageListener;
    @MockBean
    private PhemexWebSocketClient phemexWebSocketClient;
    @Autowired
    private GGrayTradeServiceImpl grayTradeUSDTService;

    @Autowired
    private StrategyServiceImpl strategyService;

    @Autowired
    private PositionService positionService;

    @Test
    void testCreateGrayTradeContext() throws Exception {
        GrayTradingContext ctx = strategyService.createStrategy("28000", 100000L);
        Assertions.assertNotNull(ctx);

        GrayTradingContext context1 = strategyService.adjustStrategy(ctx);
        Assertions.assertNotNull(context1);
    }

    @Test
    void testFoundClosePrice() {
        Long[] prices = {2787579800000L, 2815455598000L, 2843610153980L, 2872046255519L};
        long targetPrice = PriceUtils.findClosePrice("28720.4", prices, 8);
        Assertions.assertEquals(2872046255519L, targetPrice);

        targetPrice = PriceUtils.findClosePrice("28750.1", prices, 8);
        Assertions.assertEquals(0L, targetPrice);
    }
}
