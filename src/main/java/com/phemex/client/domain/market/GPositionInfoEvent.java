package com.phemex.client.domain.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * {"position_info":{"accountID":16132130003,"light":1,"marginMode":"Cross","posSide":"Long","symbol":"BTCUSDT","userID":1613213},"sequence":11178671}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GPositionInfoEvent {

    @JsonProperty("position_info")
    private PositionInfo positionInfo;

    private long sequence;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    @JsonPropertyOrder({"accountID", "light", "marginMode", "posSide", "symbol", "userID"})
    public static class PositionInfo {
        private long accountID;

        private int light;

        private String marginMode;

        private String posSide;

        private String symbol;

        private long userID;
    }
}
