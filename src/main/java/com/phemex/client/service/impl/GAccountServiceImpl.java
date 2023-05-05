package com.phemex.client.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.phemex.client.domain.GAccountPositionVo;
import com.phemex.client.httpops.HttpOps;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.service.GAccountService;
import com.phemex.client.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GAccountServiceImpl implements GAccountService {

    private final ConnectionProp apiState;

    private final HttpOps httpOps;

    @Override
    public CompletableFuture<PhemexResponse<GAccountPositionVo>> queryAccountPosition(String currency, String symbol) {
        return ClientUtils.sendRequest(
                apiState,
                httpOps,
                "/g-accounts/positions",
                "currency=" + Objects.requireNonNull(currency) + "&symbol=" + Objects.requireNonNull(symbol),
                "GET",
                null,
                new TypeReference<PhemexResponse<GAccountPositionVo>>() {
                }
        );
    }
}
