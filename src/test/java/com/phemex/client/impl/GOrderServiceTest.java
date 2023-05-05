package com.phemex.client.impl;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.phemex.client.domain.GKlineModelVo;
import com.phemex.client.listener.PhemexMessageListener;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.service.GOrder;
import com.phemex.client.service.PhemexWebSocketClient;
import com.phemex.client.service.ctx.PriceAnalysisResult;
import com.phemex.client.utils.ApacheMathLibraryUtils;
import com.phemex.client.utils.ConvertorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

class GOrderServiceTest extends BaseTest {
    @MockBean
    private PhemexMessageListener phemexMessageListener;
    @MockBean
    private PhemexWebSocketClient phemexWebSocketClient;

    @Autowired
    private GOrder gOrderService;

    @Test
    void testLoadKlines() throws ExecutionException, InterruptedException, TimeoutException {
        PhemexResponse<GKlineModelVo> res = gOrderService.queryGKlines("BTCUSDT", 60, 1000).get(5, TimeUnit.SECONDS);
        List<GKlineModelVo.GKlineVo> klines = res.getData().getKlines().stream()
                .sorted(Comparator.comparing(GKlineModelVo.GKlineVo::getTimestamp))
                .collect(Collectors.toList());

        PriceAnalysisResult analysisResult = ApacheMathLibraryUtils.doAnalysis(klines.stream()
                .map(kline -> ConvertorUtils.convertToScaleValue(kline.getClose(), 8).longValue()).collect(Collectors.toList()));
        System.out.println("all result: " + analysisResult);

        List<GKlineModelVo.GKlineVo> subKlines = klines.stream()
                .filter(kline -> kline.getTimestamp() >= 1682904300L && kline.getTimestamp() <= 1682906100L)
                .sorted(Comparator.comparing(GKlineModelVo.GKlineVo::getTimestamp))
                .collect(Collectors.toList());
        Assertions.assertNotNull(subKlines);
        // 35分钟
//        PriceAnalysisResult analysisResult1 = ApacheMathLibraryUtils.doAnalysis(subKlines.stream()
//                .map(kline -> ConvertorUtils.convertToScaleValue(kline.getClose(), 8).longValue()).collect(Collectors.toList()));
//        System.out.println("35 mins result: " + analysisResult1);

        Assertions.assertNotNull(res);

        checkPrice(klines, 5);

        System.out.println("--------------------------");
    }

    private void checkPrice(List<GKlineModelVo.GKlineVo> input, int internal) {
        PriceAnalysisResult analysisResult = null;
        for(int i=0;i < input.size() - internal; i++) {
            System.out.println(input.get(i + internal-1).getTimestamp());
        }
        for(int i=0;i < input.size() - internal; i++) {
            analysisResult = ApacheMathLibraryUtils.doAnalysis(input.subList(i, i+internal).stream()
                    .map(kline -> ConvertorUtils.convertToScaleValue(kline.getClose(), 8).longValue()).collect(Collectors.toList()));
            System.out.println(analysisResult.getStandardDeviation());
        }
    }

    private void checkVolume(List<GKlineModelVo.GKlineVo> input, int internal) {
        PriceAnalysisResult analysisResult = null;
        for(int i=0;i < input.size() - internal; i++) {
            System.out.println(input.get(i + internal - 1).getTimestamp());
        }
        for(int i=0;i < input.size() - internal; i++) {
            analysisResult = ApacheMathLibraryUtils.doAnalysis(input.subList(i, i+internal).stream()
                    .map(kline -> ConvertorUtils.convertToScaleValue(kline.getVolume(), 8).longValue()).collect(Collectors.toList()));
            System.out.println(analysisResult.getStandardDeviation());
        }
    }

}
