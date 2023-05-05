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
public class OrderbookEvent implements SymbolEvent {

    private BookEntry book;

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

        List<BookItem> asks;

        List<BookItem> bids;

    }

    @Data
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonPropertyOrder({"priceEp", "qty"})
    @EqualsAndHashCode
    public static class BookItem {

        private long priceEp;

        @EqualsAndHashCode.Exclude
        private long qty;

    }
}
