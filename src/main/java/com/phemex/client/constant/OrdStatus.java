package com.phemex.client.constant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrdStatus {

    @JsonEnumDefaultValue
    Created(0),

    Untriggered(1),

    Deactivated(2),

    Triggered(3),

    Rejected(4),

    New(5),

    PartiallyFilled(6),

    Filled(7),

    Canceled(8);

    @Getter
    private final int code;
}
