package com.phemex.client.domain.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradePushEvent implements SymbolEvent {

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private List<TradeItem> trades;

    private String symbol;

    private long sequence;

    private String type;

    @Data
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonPropertyOrder({"timestamp", "side", "priceEp", "qty"})
    public static class TradeItem {

        private long timestamp;

        private String side;

        private long priceEp;

        private long qty;
    }
}
