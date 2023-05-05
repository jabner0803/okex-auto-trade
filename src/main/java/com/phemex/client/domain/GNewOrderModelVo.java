package com.phemex.client.domain;

import com.phemex.client.constant.PosSide;
import com.phemex.client.constant.Side;
import lombok.Data;

/**
 * {
 * "createdAt": 1682585547418,
 * "symbol": "BTCUSDT",
 * "orderQtyRq": "0.006",
 * "side": 2,
 * "posSide": 2,
 * "priceRp": "28950",
 * "execQtyRq": "0.006",
 * "leavesQtyRq": "0",
 * "execPriceRp": "28966.2",
 * "orderValueRv": "173.7972",
 * "leavesValueRv": "0",
 * "cumValueRv": "173.7972",
 * "stopDirection": 0,
 * "stopPxRp": "0",
 * "trigger": 0,
 * "actionBy": 2,
 * "execFeeRv": "0.10427832",
 * "ordType": 2,
 * "ordStatus": 7,
 * "clOrdId": "45378ff",
 * "orderId": "45378ff6-bde3-4422-881f-5f1737efbd75",
 * "execStatus": 7,
 * "bizError": 0,
 * "totalPnlRv": null,
 * "avgTransactPriceRp": null,
 * "orderDetailsVos": null,
 * "tradeType": 1,
 * "updatedAt": 1682585547422
 * },
 */
@Data
public class GNewOrderModelVo {
    private long createdAt;
    private String symbol;
    private String orderQtyRq;
    private Side side;
    private PosSide posSide;
    private String priceRp;
    private String execQtyRq;
    private String leavesQtyRq;
    private String execPriceRp;
    private String orderValueRv;
    private String leavesValueRv;
    private String cumValueRv;
    private String stopDirection;
    private String stopPxRp;
    private String trigger;
    private int actionBy;
    private String execFeeRv;
    private String ordType;
    private String ordStatus;
    private String clOrdId;
    private String orderId;
    private int execStatus;
    private int bizError;
    private String totalPnlRv;
    private String avgTransactPriceRp;
    private String orderDetailsVos;
    private int tradeType;
    private long updatedAt;
}
