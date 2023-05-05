package com.phemex.client.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.phemex.client.domain.AccountPositionVo;
import com.phemex.client.httpops.HttpOps;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.service.Account;
import com.phemex.client.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountImpl implements Account {

    private final ConnectionProp apiState;

    private final HttpOps httpOps;

    public CompletableFuture<PhemexResponse<String>> depositAddress(String currency) {
        return ClientUtils.sendRequest(
                apiState,
                httpOps,
                "/phemex-user/wallets/depositAddress",
                "currency=" + currency,
                "GET",
                null,
                new TypeReference<PhemexResponse<String>>() {
                }
        );
    }

    public CompletableFuture<PhemexResponse<AccountPositionVo>> queryAccountPosition(String currency) {
        return ClientUtils.sendRequest(
                apiState,
                httpOps,
                "/accounts/accountPositions",
                "currency=" + currency,
                "GET",
                null,
                new TypeReference<PhemexResponse<AccountPositionVo>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<String>> changeLeverage(String symbol, long leverageEr) {
        return ClientUtils.sendRequest(
                apiState,
                httpOps,
                "/positions/leverage",
                "symbol=" + Objects.requireNonNull(symbol) + "&leverageEr=" + leverageEr,
                "PUT",
                null,
                new TypeReference<PhemexResponse<String>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<String>> changeRiskLimit(String symbol, long riskLimitEv) {
        return ClientUtils.sendRequest(
                apiState,
                httpOps,
                "/positions/riskLimit",
                "symbol=" + Objects.requireNonNull(symbol) + "&riskLimitEv=" + riskLimitEv,
                "PUT",
                null,
                new TypeReference<PhemexResponse<String>>() {
                }
        );
    }

    @Override
    public CompletableFuture<PhemexResponse<String>> assignPositionBalance(String symbol, long posBalanceEv) {
        return ClientUtils.sendRequest(
                apiState,
                httpOps,
                "/positions/assign",
                "symbol=" + Objects.requireNonNull(symbol) + "&posBalanceEv=" + posBalanceEv,
                "PUT",
                null,
                new TypeReference<PhemexResponse<String>>() {
                }
        );
    }

}
