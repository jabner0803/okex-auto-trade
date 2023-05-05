package com.phemex.client.service;

import com.phemex.client.prop.GrayProp;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.service.impl.StrategyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractGrayTradeService implements SmartInitializingSingleton {

    protected ConcurrentHashMap<String, GrayTradingContext> tradingStrategy = new ConcurrentHashMap<>();

    @Autowired
    protected StrategyServiceImpl strategyService;

    @Autowired
    protected GrayProp grayProp;

    @Autowired
    protected PhemexClient phemexClient;

    @Autowired
    protected PositionService positionService;

    protected GrayTradingContext initTradingStrategy(String openPriceRq, long stepRatioEv) {
        GrayTradingContext context = strategyService.createStrategy(openPriceRq, stepRatioEv);
        // check position and create close order
        positionService.adjustPositions(context);
        return context;
    }
}
