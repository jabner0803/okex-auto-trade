package com.phemex.client.service;

import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.Side;
import com.phemex.client.constant.TriggerType;
import com.phemex.client.domain.AccountPositionVo;
import com.phemex.client.domain.GAccountPositionVo;
import com.phemex.client.domain.GKlineModelVo;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.domain.OrderModelVo;
import com.phemex.client.message.PhemexResponse;

import java.util.List;

public interface PhemexClient {

    String apiKeySignature(long expiry);

    String apiKey();

    String apiSecret();

    PhemexResponse<AccountPositionVo> queryAccountPosition(String currency);

    void createContractOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType orderType);

    void createSpotOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType ordType);

    void createGContractOrder(String symbol, String orderQtyRq, String orderPriceRp, Side side, OrdType orderType, String posSide, boolean closeOnTrigger);

    void createReduceOnlyOrder(String symbol, TriggerType triggerType, OrdType orderType, Side side, boolean reduceOnly, String orderQtyRq,
                               String orderPriceRp, String takeProfitPp, String stopLossRp, String posSide);

    GAccountPositionVo queryGAccountPositions(String currency, String symbol);

    List<OrderModelVo> queryAllActiveOrders(String path, String symbol);

    List<GOrderModelVo> queryAllActiveGOrders(String symbol);

    List<GNewOrderModelVo> queryFilledGOrders(String symbol, String currency, long startImeMills, int ordStatus);

    void cancelAllOrders(String path, String symbol);

    void cancelGOrder(String orderID, String symbol, String posSide);

    void cancelAllGOrders(String symbol, boolean untriggered);

    void cancelOrderBySymbolAndOrderId(String symbol, String orderID);

    GKlineModelVo queryGKlines(String symbol, int resolution, int limit);

    @Deprecated
    default String accessTokenSignature(long expiry) {
        return apiKeySignature(expiry);
    }

    @Deprecated
    default String accessToken() {
        return apiKey();
    }
}
