package com.phemex.client.domain.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.phemex.client.domain.GAccountModelVo;
import com.phemex.client.domain.GOrderModelVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountOrderPositionEvent {

    @JsonProperty("index_market24h")
    private IndexMarket24 indexMarket24;

    private long timestamp;

    @JsonProperty("accounts_p")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<GAccountModelVo> accounts;

    @JsonProperty("orders_p")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<GOrderModelVo> orders;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    @JsonPropertyOrder({"highEp", "lastEp", "lowEp", "openEp", "symbol"})
    public static class IndexMarket24 {
        private long highEp;

        private long lastEp;

        private long lowEp;

        private long openEp;

        private String symbol;
    }
}
