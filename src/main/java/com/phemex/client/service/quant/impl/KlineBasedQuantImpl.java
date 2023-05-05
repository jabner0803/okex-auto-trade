package com.phemex.client.service.quant.impl;

import com.phemex.client.domain.GKlineModelVo;
import com.phemex.client.domain.market.PKlinePushEvent;
import com.phemex.client.prop.GrayProp;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.service.ctx.GrayTradingContext;
import com.phemex.client.service.ctx.PriceAnalysisResult;
import com.phemex.client.service.impl.StrategyServiceImpl;
import com.phemex.client.service.quant.KlineBasedQuant;
import com.phemex.client.utils.ApacheMathLibraryUtils;
import com.phemex.client.utils.ConvertorUtils;
import com.phemex.client.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KlineBasedQuantImpl implements KlineBasedQuant {

    private final GrayProp grayProp;

    private final PhemexClient phemexClient;

    private final StrategyServiceImpl strategyService;

    private static PriceAnalysisResult basePrice;

    // Timestamp -> PriceAnalysisResult Map
    private Map<Long, PriceAnalysisResult> lastClosePriceARMap = new TreeMap<>(Comparator.reverseOrder());

    // Timestamp -> LastClosePrice
    private Map<Long, Long> lastClosePriceMap = new TreeMap<>(Comparator.reverseOrder());

    private final int step = 5;

    private final int initialLength = 1000;

    @Override
    public GrayTradingContext refreshKlines(GrayTradingContext gtc, PKlinePushEvent.PKlineEntry klineEntry) throws Exception {

        long latestPriceEv = ConvertorUtils.convertToScaleValue(klineEntry.getClose(), grayProp.getScaleRatio()).longValue();
        lastClosePriceMap.put(klineEntry.getTimestamp(), latestPriceEv);
        // check price movement
        PriceAnalysisResult newPriceAR = buildLatestPAR(klineEntry);
        long preTime = TimeUtils.preTimeSeconds(klineEntry.getTimestamp(), 1);
        if (!lastClosePriceMap.containsKey(preTime)) {
            return null;
        }
        // Update lastClosePriceARMap
        lastClosePriceARMap.put(klineEntry.getTimestamp(), newPriceAR);
        long droppedTime = TimeUtils.preTimeSeconds(klineEntry.getTimestamp(), initialLength);
        lastClosePriceARMap.remove(droppedTime);
        lastClosePriceMap.remove(droppedTime);
        log.debug("Removed :{} , lastClosePriceARMap size: {}, lastClosePriceMap size: {}", droppedTime, lastClosePriceARMap.size(), lastClosePriceMap.size());
        // update basePrice
        updateBasePrice();

        // 更新策略
        return refreshStrategy(gtc, klineEntry, latestPriceEv, newPriceAR, preTime);
    }

    public PriceAnalysisResult buildLatestPAR(PKlinePushEvent.PKlineEntry klineEntry) throws Exception {
        if (basePrice == null) {
            init();
        }
        long latestPriceEv = ConvertorUtils.convertToScaleValue(klineEntry.getClose(), grayProp.getScaleRatio()).longValue();
        lastClosePriceMap.put(klineEntry.getTimestamp(), latestPriceEv);

        return ApacheMathLibraryUtils.doAnalysis(lastClosePriceMap.values().stream().limit(step).collect(Collectors.toList()));
    }


    public GrayTradingContext refreshStrategy(GrayTradingContext gtc, PKlinePushEvent.PKlineEntry klineEntry, long latestPriceEv, PriceAnalysisResult newPriceAR, long preTime) throws Exception {
        int moveDirection = latestPriceEv - lastClosePriceMap.get(preTime) > 0 ? 1 : -1;
        int fluctuateRatio = Math.toIntExact(newPriceAR.getStandardDeviation() / basePrice.getMeanValue());
        GrayTradingContext newCtx;
        if (fluctuateRatio > grayProp.getRiskLevel()) {
            newCtx = strategyService.quickMoveStrategy(gtc, klineEntry, moveDirection, fluctuateRatio);
        } else {
            newCtx = strategyService.quitQuickMoveStrategy(gtc, klineEntry);
        }
        return newCtx;
    }

    @SneakyThrows
    @Override
    public void afterSingletonsInstantiated() {
        if (basePrice == null) {
            init();
        }
    }

    private void init() throws Exception {
        GKlineModelVo res = phemexClient.queryGKlines("BTCUSDT", 60, initialLength);
        if (res == null || res.getKlines() == null) {
            throw new Exception("GKlineModelVo load failed");
        }
        res.getKlines().forEach(kline -> lastClosePriceMap.put(kline.getTimestamp(), ConvertorUtils.convertToScaleValue(kline.getClose(), grayProp.getScaleRatio()).longValue()));

        Map<Long, PriceAnalysisResult> priceList = new HashMap<>();
        for(int i = 0;i< res.getKlines().size() - step; i++) {
            priceList.put(res.getKlines().get(i + step - 1).getTimestamp(), ApacheMathLibraryUtils.doAnalysis(res.getKlines().subList(i, i + step)
                    .stream().map(kline -> ConvertorUtils.convertToScaleValue(kline.getClose(), 8).longValue()).collect(Collectors.toList())));
        }
        lastClosePriceARMap.putAll(priceList);
        updateBasePrice();
    }

    private void updateBasePrice() {
        // update basePrice
        log.debug("Before basePrice is {}", basePrice);
        basePrice = ApacheMathLibraryUtils.doAnalysis(lastClosePriceARMap.values().stream().map(PriceAnalysisResult::getStandardDeviation).collect(Collectors.toList()));
        log.debug("After basePrice is {}", basePrice);
    }
}
