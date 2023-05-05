package com.phemex.client.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GCreateOrderRequest {

    private String symbol;

    private String posSide;
    private String side;
    @Builder.Default
    private String orderQtyRq = "0";
    private String priceRp;
    private String displayQtyRq;
    private String stopPxRp;
    private String clOrdID;

    private String pegOffsetValueRp;
    private String pegOffsetProportionRr;
    @Builder.Default
    private String pegPriceType = "UNSPECIFIED";
    @Builder.Default
    private String ordType = "Limit";
    @Builder.Default
    private String timeInForce = "GoodTillCancel";

    private boolean reduceOnly;

    private boolean closeOnTrigger;

    private String takeProfitRp;

    private String stopLossRp;

    @Builder.Default
    private String triggerType = "UNSPECIFIED";

    @Builder.Default
    private String tpSlTs = "UNSPECIFIED";

    private String tpTrigger;

    private String slTrigger;
    @Builder.Default
    private String text = "";

    private String actionBy;
}
