package com.phemex.client.domain.market.response;

import lombok.Data;

@Data
public class ExchangePushResponse<T> {

    private String tag;

    private T data;

}
