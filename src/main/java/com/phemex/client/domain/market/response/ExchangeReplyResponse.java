package com.phemex.client.domain.market.response;

import lombok.Data;

@Data
public class ExchangeReplyResponse {

    private ExchangeError error;

    private Object result;

    private Long id;
}
