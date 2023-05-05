package com.phemex.client.domain;

import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.Side;
import lombok.Data;

/**
 * Response sample * Full order { "code": 0, "msg": "", "data": { "rows": [ { "bizError": 0, "orderID": "c2621102-1cc0-4686-b520-9879311bcc26",
 * "clOrdID": "", "symbol": "BTCUSDT", "side": "Sell", "actionTimeNs": 1678163665765381733, "transactTimeNs": 1678163665769528669,
 * "orderType": "Limit", "priceRp": "22490.4", "orderQtyRq": "0.005", "displayQtyRq": "0.005", "timeInForce": "GoodTillCancel",
 * "reduceOnly": false, "closedPnlRv": "0", "closedSizeRq": "0", "cumQtyRq": "0", "cumValueRv": "0", "leavesQtyRq": "0.005",
 * "leavesValueRv": "112.452", "stopDirection": "UNSPECIFIED", "stopPxRp": "0", "trigger": "UNSPECIFIED", "pegOffsetValueRp": "0",
 * "pegOffsetProportionRr": "0", "execStatus": "New", "pegPriceType": "UNSPECIFIED", "ordStatus": "New", "execInst": "CloseOnTrigger",
 * "takeProfitRp": "0", "stopLossRp": "0" } ], "nextPageArg": "" }
 */
@Data
public class GOrderModelVo {
    private int bizError;
    private String orderID;
    private String clOrdID;
    private String symbol;
    private Side side;
    private long actionTimeNs;
    private long transactTimeNs;
    private OrdType orderType;
    private String priceRp;
    private String orderQtyRq;
    private String displayQtyRq;
    private String timeInForce;
    private boolean reduceOnly;
    private String closedPnlRv;
    private String closedSizeRq;
    private String cumQtyRq;
    private String cumValueRv;
    private String leavesQtyRq;
    private String leavesValueRv;
    private String stopDirection;
    private String stopPxRp;
    private String trigger;
    private String pegOffsetValueRp;
    private String pegOffsetProportionRr;
    private String execStatus;
    private String pegPriceType;
    private String ordStatus;
    private String execInst;
    private String takeProfitRp;
    private String stopLossRp;

}


