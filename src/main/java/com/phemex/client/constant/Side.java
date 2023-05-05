package com.phemex.client.constant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Side {

    @JsonEnumDefaultValue
    None(0),

    Buy(1),

    Sell(2);


    @Getter
    private final int code;
}
