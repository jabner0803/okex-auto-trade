package com.phemex.client.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GKlineModelVo {
    private long total;

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonProperty("rows")
    private List<GKlineVo> klines;

    @Data
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    @JsonPropertyOrder({"timestamp", "interval", "last_close", "open", "high", "low", "close", "volume", "turnover", "symbol"})
    public static class GKlineVo {
        private long timestamp;

        private int interval;

        @JsonProperty("last_close")
        private String lastClose;

        private String open;

        private String high;

        private String low;

        private String close;

        private String volume;

        private String turnover;

        private String symbol;
    }
}
