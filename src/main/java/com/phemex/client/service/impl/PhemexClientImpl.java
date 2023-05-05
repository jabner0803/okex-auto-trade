package com.phemex.client.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phemex.client.constant.OrdType;
import com.phemex.client.constant.PlatformType;
import com.phemex.client.constant.QtyType;
import com.phemex.client.constant.Side;
import com.phemex.client.constant.TimeInForce;
import com.phemex.client.constant.TriggerType;
import com.phemex.client.domain.AccountPositionVo;
import com.phemex.client.domain.GAccountPositionVo;
import com.phemex.client.domain.GKlineModelVo;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.domain.OrderModelVo;
import com.phemex.client.message.CreateOrderRequest;
import com.phemex.client.message.CreateSpotOrderRequest;
import com.phemex.client.message.GCreateOrderRequest;
import com.phemex.client.message.PageResultVo;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.service.Account;
import com.phemex.client.service.GAccountService;
import com.phemex.client.service.GOrder;
import com.phemex.client.service.Order;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhemexClientImpl implements PhemexClient, SmartInitializingSingleton {

    private final ConnectionProp connectionProp;

    private final Order order;

    private final GOrder gOrder;

    private final Account account;

    private final GAccountService gAccountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String apiKeySignature(long expiry) {
        return ClientUtils.signAccessToken(connectionProp.getApiKey(), expiry,
                Objects.requireNonNull(connectionProp.getApiSecret()).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String apiKey() {
        if (connectionProp == null) {
            log.error("connectionProp is null");
            return null;
        }
        return connectionProp.getApiKey();
    }

    @Override
    public String apiSecret() {
        return connectionProp.getApiSecret();
    }
    @Override
    public PhemexResponse<AccountPositionVo> queryAccountPosition(String currency) {
        try {
            return this.account.queryAccountPosition(currency).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createContractOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType orderType) {
        PhemexResponse<OrderModelVo> res = null;
        try {
            res = order.createOrder(CreateOrderRequest.builder()
                    .symbol(symbol)
                    .clOrdID(UUID.randomUUID().toString())
                    .orderQty(orderQty)
                    .priceEp(orderPrice)
                    .ordType(orderType)
                    .side(side)
                    .timeInForce(TimeInForce.GoodTillCancel)
                    .build()).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("createContractOrder exception: ", e);
        }
        log.info("Limit Order created {}", res.getData());
    }

    @Override
    public void createGContractOrder(String symbol, String orderQtyRq, String orderPriceRp, Side side, OrdType orderType, String posSide, boolean closeOnTrigger) {
        PhemexResponse<GOrderModelVo> res = null;
        try {
            res = gOrder.createOrder(GCreateOrderRequest.builder()
                            .symbol(symbol)
                            .clOrdID(UUID.randomUUID().toString())
                            .orderQtyRq(orderQtyRq)
                            .priceRp(orderPriceRp)
                            .ordType(orderType.name())
                            .side(side.name())
                            .posSide(posSide)
                            .closeOnTrigger(closeOnTrigger)
//                    .timeInForce(TimeInForce.PostOnly.name())
                            .build())
                    .get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("createGContractOrder exception: {}, {}", e, res);
        }
        log.info("Limit Order created {}", res);
    }

    @Override
    public void createReduceOnlyOrder(String symbol, TriggerType triggerType, OrdType orderType, Side side, boolean reduceOnly, String orderQtyRq,
                                      String orderPriceRp, String takeProfitPp, String stopLossRp, String posSide) {
        PhemexResponse<GOrderModelVo> res = null;
        GCreateOrderRequest request = null;
        try {
             request = GCreateOrderRequest.builder()
                    .actionBy("FromOrderPlacement")
                    .symbol(symbol)
                    .side(side.name())
                    .triggerType(triggerType.name())
                    .clOrdID(UUID.randomUUID().toString())
                    .closeOnTrigger(reduceOnly)
                    .reduceOnly(reduceOnly)
                    .ordType(orderType.name())
                    .timeInForce(TimeInForce.GoodTillCancel.name())
                    .tpTrigger(side == Side.Buy ? "" : triggerType.name())
                    .slTrigger(side == Side.Buy ? "" : triggerType.name())
                    .orderQtyRq(orderQtyRq)
                    .displayQtyRq(orderQtyRq)
                    .priceRp(orderPriceRp)
                    .takeProfitRp(side == Side.Buy ? "0.0" : takeProfitPp)
                    .stopLossRp(side == Side.Buy ? "0.0" : stopLossRp)
                    .stopPxRp(orderPriceRp)
                    .posSide(posSide)
                    .build();
            res = gOrder.createOrder(request).get(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("createReduceOnlyOrder exception: ", ex);
        }
        log.info("createReduceOnlyOrder created {}", res);
    }

    @Override
    public GAccountPositionVo queryGAccountPositions(String currency, String symbol) {
        PhemexResponse<GAccountPositionVo> ret = null;
        try {
            ret = gAccountService.queryAccountPosition(currency, symbol).get(5, TimeUnit.SECONDS);
            return ret.getData();
        } catch (Exception ex) {
            log.error("queryGAccountPositions, ", ex);
        }
        return null;
    }

    @Override
    public void createSpotOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType ordType) {
        PhemexResponse<OrderModelVo> res = null;
        try {
            CreateSpotOrderRequest spotOrder = CreateSpotOrderRequest.builder()
                    .symbol(symbol)
                    .clOrdID(UUID.randomUUID().toString())
                    .quoteQtyEv(orderQty)
                    .baseQtyEv(0L)
                    .stopPxEp(0L)
                    .priceEp(orderPrice)
                    .side(side)
                    .ordType(ordType)
                    .qtyType(QtyType.ByQuote)
                    .trigger(TriggerType.ByMarkPrice)
                    .platform(PlatformType.API)
                    .timeInForce(TimeInForce.GoodTillCancel)
                    .text("")
                    .build();
            res = order.createSpotOrder(spotOrder).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        log.info("Limit Order created {}", res.getData());
    }

    @Override
    public List<OrderModelVo> queryAllActiveOrders(String path, String symbol) {

        try {
            PhemexResponse<List<OrderModelVo>> orders = order.queryActiveOrders(path, symbol).get(5, TimeUnit.SECONDS);
            return orders.getData();
        } catch (Exception e) {
            log.error("queryAllActiveOrders error: ", e);
        }
        return null;
    }

    public List<GOrderModelVo> queryAllActiveGOrders(String symbol) {
        try {
            PhemexResponse<PageResultVo> orders = gOrder.queryActiveOrders(symbol).get(5, TimeUnit.SECONDS);
            if (orders.getData() == null) {
                return new ArrayList<>();
            }
            PageResultVo vo = orders.getData();
            return objectMapper.convertValue(vo.getRows(), new TypeReference<List<GOrderModelVo>>() {
            });
        } catch (Exception e) {
            log.error("queryAllActiveOrders error: ", e);
        }
        return null;
    }

    @Override
    public List<GNewOrderModelVo> queryFilledGOrders(String symbol, String currency, long startImeMills, int ordStatus) {
        try {
            PhemexResponse<List<GNewOrderModelVo>> orders = gOrder.queryExecutedGContractOrders(symbol, currency, startImeMills, ordStatus).get(5, TimeUnit.SECONDS);
            if (orders.getData() == null) {
                return new ArrayList<>();
            }
            return orders.getData();

        } catch (Exception ex) {
            log.error("queryFilledGOrders got exception: {}", ex);
        }
        return new ArrayList<>();
    }

    @Override
    public void cancelAllOrders(String root, String symbol) {
        log.info("Trying to cancelAllOrders for symbol: {}", symbol);
        try {
            order.cancelAll(root, symbol, true, null);
        } catch (Exception ex) {
            log.error("cancelAllOrders got exception: ", ex);
        }
    }

    @Override
    public void cancelOrderBySymbolAndOrderId(String symbol, String orderID) {
        log.info("Starting to cancel order with symbol:{} and orderID: {}", symbol, orderID);
        try {
            order.cancelOrder(symbol, orderID);
        } catch (Exception ex) {
            log.error("cancelOrderBySymbolAndOrderId got exception: ", ex);
        }
    }

    @Override
    public void cancelAllGOrders(String symbol, boolean untriggered) {
        try {
            gOrder.cancelAllGOrders(symbol, untriggered, null);
        } catch (Exception ex) {
            log.error("cancelAllGOrders got exception : ", ex);
        }
    }

    @Override
    public void cancelGOrder(String orderID, String symbol, String posSide) {
        try {
            gOrder.cancelGOrder(orderID, symbol, posSide);
        } catch (Exception ex) {
            log.error("cancelGOrder got exception : ", ex);
        }
    }

    @Override
    public GKlineModelVo queryGKlines(String symbol, int resolution, int limit) {
        try {
            PhemexResponse<GKlineModelVo> response = gOrder.queryGKlines(symbol, resolution, limit).get(5, TimeUnit.SECONDS);
            if (response.getData() != null) {
                return response.getData();
            }
        } catch (Exception ex) {
            log.error("queryGKlines got exception: ", ex);
        }
        return null;
    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}
