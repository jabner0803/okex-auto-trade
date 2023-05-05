package com.phemex.client.constant;

public enum ExecInst {
    /**
     * <code>None = 0;</code>
     */
    None(0),
    /**
     * <pre>
     * 带上这个标识的订单,成交后保证持仓: 0 &lt;= x &lt;= 现有size
     * </pre>
     *
     * <code>ReduceOnly = 1;</code>
     */
    ReduceOnly(1),
    /**
     * <pre>
     * 单纯带上close-on-trigger没有用 不一定能保证效果
     * </pre>
     *
     * <code>NotUse2 = 2;</code>
     */
    NotUse2(2),
    /**
     * <pre>
     * (包含reduce-only) 且当成本不足时, cancel掉同方向的其他更低优先级订单(从最低优先级开始cancel)
     * </pre>
     *
     * <code>CloseOnTrigger = 3;</code>
     */
    CloseOnTrigger(3),
    UNRECOGNIZED(-1),
    ;

    private final int value;

    private ExecInst(int value) {
        this.value = value;
    }

}
