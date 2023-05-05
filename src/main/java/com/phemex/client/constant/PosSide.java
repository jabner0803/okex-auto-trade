package com.phemex.client.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PosSide {

    Invalid(0),
    Long(1),
    Short(2),
    Merged(3);

    @Getter
    private final int code;
}
