package com.phemex.client.domain.market.response;

import lombok.Data;

@Data
public class ExchangeError {

    private int code;

    private String message;
}
