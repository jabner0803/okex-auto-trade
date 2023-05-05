package com.phemex.client.service.quant.impl;

import com.phemex.client.domain.market.OrderBookPEvent;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.service.ctx.PriceAnalysisResult;
import com.phemex.client.service.quant.OrderBookBasedQuant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderBookBasedQuantImpl implements OrderBookBasedQuant {

    // Timestamp -> PriceAnalysisResult Map
    private Map<Long, PriceAnalysisResult> lastClosePriceARMap = new TreeMap<>(Comparator.reverseOrder());

    // Timestamp -> LastClosePrice
    private Map<Long, Long> lastClosePriceMap = new TreeMap<>(Comparator.reverseOrder());


    @Override
    public void refresh(GrayTradingContext context, OrderBookPEvent bookPEvent) {

    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}
