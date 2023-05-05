package com.phemex.client.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.phemex.client.domain.GKlineModelVo;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.httpops.HttpOps;
import com.phemex.client.message.GCreateOrderRequest;
import com.phemex.client.message.PageResultVo;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.service.GOrder;
import com.phemex.client.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GOrderImpl implements GOrder {

    private final ConnectionProp connectionProp;

    private final HttpOps httpOps;

    @Override
    public CompletableFuture<PhemexResponse<GOrderModelVo>> createOrder(GCreateOrderRequest createOrderRequest) {
        log.info("createOrder: {}", createOrderRequest);
        String body = null;
        try {
            body = ClientUtils.objectMapper.writeValueAsString(createOrderRequest);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing request body", e);
        }
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/g-orders",
                null,
                "POST",
                body,
                new TypeReference<PhemexResponse<GOrderModelVo>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<PageResultVo>> queryActiveOrders(String symbol) {

        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/g-orders/activeList",
                "symbol=" + Objects.requireNonNull(symbol),
                "GET",
                null,
                new TypeReference<PhemexResponse<PageResultVo>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<String>> cancelAllGOrders(String symbol, boolean untriggered, String text) {
        String qStr = "symbol=" + Objects.requireNonNull(symbol) + "&untriggered=" + untriggered;
        if (text != null) {
            qStr += "&text=" + text;
        }
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/g-orders/all",
                qStr,
                "DELETE",
                null,
                new TypeReference<PhemexResponse<String>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<String>> cancelGOrder(String orderID, String symbol, String posSide) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/g-orders/cancel",
                "orderID=" + Objects.requireNonNull(orderID) + "&posSide=" + Objects.requireNonNull(posSide) + "&symbol=" + Objects.requireNonNull(symbol),
                "DELETE",
                null,
                new TypeReference<PhemexResponse<String>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<List<GNewOrderModelVo>>> queryExecutedGContractOrders(String symbol, String currency, long startTime, int orderStatus) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/exchange/order/v2/orderList",
                "symbol=" + Objects.requireNonNull(symbol) + "&currency=" + Objects.requireNonNull(currency) + "&start=" + Objects.requireNonNull(startTime) + "&ordStatus=" + Objects.requireNonNull(orderStatus),
                "GET",
                null,
                new TypeReference<PhemexResponse<List<GNewOrderModelVo>>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<GKlineModelVo>> queryGKlines(String symbol, int resolution, int limit) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/exchange/public/md/v2/kline/last",
                "symbol=" + Objects.requireNonNull(symbol) + "&resolution=" + Objects.requireNonNull(resolution) + "&limit=" + Objects.requireNonNull(limit),
                "GET",
                null,
                new TypeReference<PhemexResponse<GKlineModelVo>>() {
                }
        );
    }

    /**
     * query from engine directly
     * @param symbol
     * @param startTimeNs
     * @return
     */
    @Override
    public CompletableFuture<PhemexResponse<GOrderModelVo>> queryHistoryOrders(String symbol, long startTimeNs) {
        return ClientUtils.sendRequest(
                connectionProp,
                httpOps,
                "/api-data/g-futures/orders",
                "symbol=" + Objects.requireNonNull(symbol) + "&start=" + Objects.requireNonNull(startTimeNs),
                "GET",
                null,
                new TypeReference<PhemexResponse<GOrderModelVo>>() {
                }
        );
    }
}
