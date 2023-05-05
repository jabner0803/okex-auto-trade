package com.phemex.client.service;

import com.phemex.client.domain.GAccountPositionVo;
import com.phemex.client.message.PhemexResponse;

import java.util.concurrent.CompletableFuture;

public interface GAccountService {

    CompletableFuture<PhemexResponse<GAccountPositionVo>> queryAccountPosition(String currency, String symbol);

}
