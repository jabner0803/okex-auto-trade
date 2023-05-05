package com.phemex.client.constant;

public enum QtyType {

    UNSPECIFIED(0),

    ByBase(1),
    ByQuote(2),
    UNRECOGNIZED(-1),
    ;

    public static QtyType forNumber(int value) {
        switch (value) {
            case 0: return UNSPECIFIED;
            case 1: return ByBase;
            case 2: return ByQuote;
            default: return null;
        }
    }

    private final int value;

    private QtyType(int value) {
        this.value = value;
    }
}
