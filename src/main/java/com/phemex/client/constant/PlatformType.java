package com.phemex.client.constant;

public enum PlatformType {

    UNSPECIFIED(0),
    /**
     * <code>WEB = 1;</code>
     */
    WEB(1),
    /**
     * <code>H5 = 2;</code>
     */
    H5(2),
    /**
     * <code>IOS = 3;</code>
     */
    IOS(3),
    /**
     * <code>ANDROID = 4;</code>
     */
    ANDROID(4),
    /**
     * <code>API = 5;</code>
     */
    API(5),
    /**
     * <code>FIXPROTO = 6;</code>
     */
    FIXPROTO(6),
    /**
     * <code>WEB_NON_PREMIUM = 7;</code>
     */
    WEB_NON_PREMIUM(7),
    /**
     * <code>H5_NON_PREMIUM = 8;</code>
     */
    H5_NON_PREMIUM(8),
    /**
     * <code>IOS_NON_PREMIUM = 9;</code>
     */
    IOS_NON_PREMIUM(9),
    /**
     * <pre>
     * ...
     * </pre>
     *
     * <code>ANDROID_NON_PREMIUM = 10;</code>
     */
    ANDROID_NON_PREMIUM(10),
    UNRECOGNIZED(-1),
    ;

    private final int value;

    private PlatformType(int value) {
        this.value = value;
    }
}
