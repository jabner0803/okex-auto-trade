package com.phemex.client.message;

import com.phemex.client.constant.ExecInst;
import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.PlatformType;
import com.phemex.client.constant.QtyType;
import com.phemex.client.constant.Side;
import com.phemex.client.constant.TimeInForce;
import com.phemex.client.constant.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateSpotOrderRequest {
    String symbol;
    String clOrdID;
    Side side;
    QtyType qtyType;
    long priceEp;
    long baseQtyEv;
    long quoteQtyEv;
    OrdType ordType;
    TimeInForce timeInForce;
    ExecInst execInst;
    long stopPxEp;
    TriggerType trigger;
    PlatformType platform;
    String text;

}
