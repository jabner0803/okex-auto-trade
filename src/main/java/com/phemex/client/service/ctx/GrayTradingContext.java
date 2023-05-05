package com.phemex.client.service.ctx;

import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.domain.GPositionVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrayTradingContext extends AbstractGrayTradingContext{

    private String symbol;

    private Integer steps;

    /**
     * Scale ratio 10000_0000
     */
    private Long stepRatioEv;

    /**
     * 看空的价格，高于基准价
     */
    private Long[] shortPrices;

    /**
     * 看多的价格，低于基准价
     */
    private Long[] longPrices;

    /**
     * 放大值
     */
    private long orderQtyEv;
    /**
     * 风控价格系数
     */
    private Long riskThresholdRatioEv;

    private long startTime;

    private long openPrice;

    private long upperThresholdPrice;

    private long lowThresholdPrice;

    /**
     * 最新的价格，实时更新
     */
    private String currencyPriceRq;
    @Builder.Default
    private Integer scaleRatio = 8;

    @Builder.Default
    private long version = 1;

    private List<GOrderModelVo> activeOrders;

    private List<GPositionVo> positions;

    private PriceAnalysisResult priceAnalysisResult;

    @Builder.Default
    private long latestFilledOrderTimeMills = Instant.now().toEpochMilli();

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private int fluctuateRatio = 0;
}
