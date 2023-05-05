package com.phemex.client.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.phemex.client.domain.OrderModelVo;
import com.phemex.client.domain.PagedResult;
import com.phemex.client.domain.TradeModelVo;
import com.phemex.client.domain.WebResultVo;
import com.phemex.client.httpops.HttpOps;
import com.phemex.client.message.CreateOrderRequest;
import com.phemex.client.message.CreateSpotOrderRequest;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.service.Order;
import com.phemex.client.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderImpl implements Order {

    private final ConnectionProp connectionProp;

    private final HttpOps httpOps;

    @Override
    public CompletableFuture<PhemexResponse<OrderModelVo>> createOrder(CreateOrderRequest createOrderRequest) {
        String body = null;
        try {
            body = ClientUtils.objectMapper.writeValueAsString(createOrderRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing request body", e);
        }
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/orders",
                null,
                "POST",
                body,
                new TypeReference<PhemexResponse<OrderModelVo>>() {
                }
        );
    }

    public CompletableFuture<PhemexResponse<OrderModelVo>> createSpotOrder(CreateSpotOrderRequest spotOrderRequest) {
        String body = null;
        try {
            body = ClientUtils.objectMapper.writeValueAsString(spotOrderRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing request body", e);
        }
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/spot/orders",
                null,
                "POST",
                body,
                new TypeReference<PhemexResponse<OrderModelVo>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<OrderModelVo>> amendOrder(String symbol,
                                                                      String orderID,
                                                                      String origClOrdID,
                                                                      String clOrdID,
                                                                      long priceEp,
                                                                      long orderQty,
                                                                      long stopPxEp,
                                                                      long takeProfitEp,
                                                                      long stopLossEp,
                                                                      long pegOffsetEp
    ) {
        StringBuilder qStrBuilder = new StringBuilder();
        qStrBuilder.append("symbol=" + Objects.requireNonNull(symbol));
        qStrBuilder.append("&orderID=" + Objects.requireNonNull(orderID));

        if (origClOrdID != null && clOrdID != null) {
            qStrBuilder.append("&origClOrdID=" + origClOrdID);
            qStrBuilder.append("&clOrdID=" + clOrdID);
        }

        if (priceEp != 0) {
            qStrBuilder.append("&priceEp=" + priceEp);
        }

        if (orderQty != 0) {
            qStrBuilder.append("&orderQty=" + orderQty);
        }

        if (stopPxEp != 0) {
            qStrBuilder.append("&stopPxEp=" + stopPxEp);
        }

        if (takeProfitEp != 0) {
            qStrBuilder.append("&takeProfitEp=" + takeProfitEp);
        }

        if (stopLossEp != 0) {
            qStrBuilder.append("&stopLossEp=" + stopLossEp);
        }

        if (pegOffsetEp != 0) {
            qStrBuilder.append("&pegOffsetEp=" + pegOffsetEp);
        }

        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/orders/replace",
                qStrBuilder.toString(),
                "PUT",
                null,
                new TypeReference<PhemexResponse<OrderModelVo>>() {
                });
    }

    @Override
    public CompletableFuture<PhemexResponse<OrderModelVo>> cancelOrder(String symbol, String orderID) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/orders/cancel",
                "symbol=" + Objects.requireNonNull(symbol) + "&orderID=" + Objects.requireNonNull(orderID),
                "DELETE",
                null,
                new TypeReference<PhemexResponse<OrderModelVo>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<OrderModelVo>> queryOpenOrder(String symbol, String orderID) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/orders/active",
                "symbol=" + Objects.requireNonNull(symbol) + "&orderID=" + Objects.requireNonNull(orderID),
                "GET",
                null,
                new TypeReference<PhemexResponse<OrderModelVo>>() {
                }
        );
    }

    public CompletableFuture<PhemexResponse<List<OrderModelVo>>> queryActiveOrders(String path, String symbol) {
        String query_path = StringUtils.isEmpty(path) ? "/orders/activeList" : path;
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                query_path,
                "symbol=" + Objects.requireNonNull(symbol),
                "GET",
                null,
                new TypeReference<PhemexResponse<List<OrderModelVo>>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<List<OrderModelVo>>> queryOrder(String symbol, String orderID) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/exchange/order",
                "symbol=" + Objects.requireNonNull(symbol) + "&orderID=" + Objects.requireNonNull(orderID),
                "GET",
                null,
                new TypeReference<PhemexResponse<List<OrderModelVo>>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<List<OrderModelVo>>> queryOrderByClOrdID(String symbol, String clOrdID) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/exchange/order",
                "symbol=" + Objects.requireNonNull(symbol) + "&clOrdID=" + Objects.requireNonNull(clOrdID),
                "GET",
                null,
                new TypeReference<PhemexResponse<List<OrderModelVo>>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<PagedResult<OrderModelVo>>> listHistoryOrders(String symbol, long start, long end, long offset, int limit) {
        int lmt = limit == 0 ? 50 : limit;
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/exchange/order/list",
                "symbol=" + Objects.requireNonNull(symbol) + "&withCount=true" + "&start=" + start + "&end=" + end + "&offset=" + offset + "&limit=" + lmt,
                "GET",
                null,
                new TypeReference<PhemexResponse<PagedResult<OrderModelVo>>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<PagedResult<TradeModelVo>>> listTrades(String symbol, long start, long end, long offset, int limit, List<String> tradeType) {
        int lmt = limit == 0 ? 50 : limit;
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/exchange/order/trade",
                "symbol=" + Objects.requireNonNull(symbol) + "&withCount=true" + "&start=" + start + "&end=" + end + "&offset=" + offset + "&limit=" + lmt,
                "GET",
                null,
                new TypeReference<PhemexResponse<PagedResult<TradeModelVo>>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<String>> cancelAll(String root, String symbol, boolean untriggered, String text) {
        String qStr = "symbol=" + Objects.requireNonNull(symbol) + "&untriggered=" + untriggered;
        if (text != null) {
            qStr += "&text=" + text;
        }
        String path = StringUtils.isEmpty(root) ? "/orders/all" : "/g-orders/all";

        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                path,
                qStr,
                "DELETE",
                null,
                new TypeReference<PhemexResponse<String>>() {
                }
        );
    }



    @Override
    public CompletableFuture<PhemexResponse<WebResultVo>> listOpenOrders(String symbol) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/orders/activeList",
                "symbol=" + Objects.requireNonNull(symbol),
                "GET",
                null,
                new TypeReference<PhemexResponse<WebResultVo>>() {
                });
    }


}
