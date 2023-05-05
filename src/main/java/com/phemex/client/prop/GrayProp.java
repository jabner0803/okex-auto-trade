package com.phemex.client.prop;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Setter
@Getter
@ConfigurationProperties(prefix = "phemex.client.subscribe")
public class GrayProp {

    private String baseCurrency;
    private String currency;

    /**
     * 建仓初始价格
     */
    private long initPriceEv;

    /**
     * 相邻网格之间的价格差
     */
    private Long stepRatioEv;

    /**
     * 网格数目
     */
    private Integer steps;

    /**
     * 风控价格系数
     */
    private Long riskThresholdRatioEv;

    /**
     * 申购金额 USDT based
     */
    private long orderQty;

    private Integer scaleRatio = 8;

    private String symbol;

    private String spot;

    private String contract;

    private String groups;

    private String weights;

    private int riskLevel = 4;
}
