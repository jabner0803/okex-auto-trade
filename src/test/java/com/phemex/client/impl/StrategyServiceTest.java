package com.phemex.client.impl;

import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.listener.PhemexMessageListener;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.service.GOrder;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.service.impl.StrategyServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.CompletableFuture;

class StrategyServiceTest extends BaseTest{

    @Autowired
    private StrategyServiceImpl strategyService;

    @MockBean
    private PhemexMessageListener phemexMessageListener;

    @MockBean
    private PhemexClient phemexClient;

//    @MockBean
//    private GOrder gOrder;
    @Test
    void testBuildStrategy() {
//        Mockito.when(phemexClient.queryAllActiveGOrders(Mockito.anyString())).thenReturn()
        GrayTradingContext context = strategyService.createStrategy("29350.6", 100000L);
        Assertions.assertNotNull(context);
    }
}
