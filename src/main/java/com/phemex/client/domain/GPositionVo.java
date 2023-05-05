package com.phemex.client.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GPositionVo {
    private long accountID;

    private String symbol;

    private String currency;

    private String side;

    private String positionStatus;

    private boolean crossMargin;

    private String leverageRr;

    private String initMarginReqRr;

    private String maintMarginReqRr;

    private String riskLimitRv;

    private String sizeRq;

    private String valueRv;

    private String avgEntryPriceRp;

    private String avgEntryPrice;

    private String posCostRv;

    private String assignedPosBalanceRv;

    private String bankruptCommRv;

    private String bankruptPriceRp;

    private String positionMarginRv;

    private String liquidationPriceRp;

    private String deleveragePercentileRr;

    private String buyValueToCostRr;

    private String sellValueToCostRr;

    private String markPriceRp;

    private String estimatedOrdLossRv;

    private String usedBalanceRv;

    private String cumClosedPnlRv;

    private String cumFundingFeeRv;

    private String cumTransactFeeRv;

    private long transactTimeNs;

    /**
     * taker fee rate
     */
    private String takerFeeRateRr;

    /**
     * maker fee rate
     */
    private String makerFeeRateRr;

    private long term;

    private long lastTermEndTimeNs;

    private long lastFundingTimeNs;

    private String curTermRealisedPnlRv;

    private long execSeq;

    private String posSide;
    private String posMode;

}
