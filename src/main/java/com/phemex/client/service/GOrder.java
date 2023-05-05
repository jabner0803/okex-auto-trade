package com.phemex.client.service;

import com.phemex.client.domain.GKlineModelVo;
import com.phemex.client.domain.GNewOrderModelVo;
import com.phemex.client.domain.GOrderModelVo;
import com.phemex.client.message.GCreateOrderRequest;
import com.phemex.client.message.PageResultVo;
import com.phemex.client.message.PhemexResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GOrder {

    CompletableFuture<PhemexResponse<GOrderModelVo>> createOrder(GCreateOrderRequest createOrderRequest);

    CompletableFuture<PhemexResponse<PageResultVo>> queryActiveOrders(String symbol);

    CompletableFuture<PhemexResponse<String>> cancelAllGOrders(String symbol, boolean untriggered, String text);

    CompletableFuture<PhemexResponse<String>> cancelGOrder(String orderID, String symbol, String posSide);

    CompletableFuture<PhemexResponse<List<GNewOrderModelVo>>> queryExecutedGContractOrders(String symbol, String currency, long startTime, int orderStatus);

    CompletableFuture<PhemexResponse<GKlineModelVo>> queryGKlines(String symbol, int resolution, int limit);

    CompletableFuture<PhemexResponse<GOrderModelVo>> queryHistoryOrders(String symbol, long startTimeNs);
}
