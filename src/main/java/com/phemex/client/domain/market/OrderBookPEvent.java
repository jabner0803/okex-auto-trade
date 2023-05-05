package com.phemex.client.domain.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBookPEvent implements SymbolEvent {

    private OrderBookPEvent.BookEntry orderbook_p;

    private int depth;

    private long sequence;

    private long timestamp;

    private String symbol;

    private String type;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BookEntry {

        List<OrderBookPEvent.BookItem> asks;

        List<OrderBookPEvent.BookItem> bids;

    }

    @Data
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonPropertyOrder({"priceRq", "qtyRq"})
    @EqualsAndHashCode
    public static class BookItem {

        private String priceRq;

        @EqualsAndHashCode.Exclude
        private String qtyRq;

    }
}
