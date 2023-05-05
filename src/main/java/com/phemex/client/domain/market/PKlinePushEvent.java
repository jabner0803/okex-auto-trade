package com.phemex.client.domain.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PKlinePushEvent implements SymbolEvent {
    // in-seconds
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonProperty("kline_p")
    private List<PKlinePushEvent.PKlineEntry> kline;

    private long sequence;

    private String symbol;

    private String type;

    @Data
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    @JsonPropertyOrder({"timestamp", "interval", "last_close", "open", "high", "low", "close", "volume", "turnover"})
    static public class PKlineEntry {

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

        public String toStr() {
            return Joiner.on(",").join(timestamp, interval, lastClose, open, high, low, close, volume, turnover);
        }

        public KlinePushEvent.KlineEntry fromStr(String str) {
            List<Long> items = Splitter.on(",").splitToList(str)
                    .stream()
                    .map(Long::parseLong).collect(Collectors.toList());
            return KlinePushEvent.KlineEntry.builder()
                    .timestamp(items.get(0))
                    .interval((int) items.get(1).longValue())
                    .lastClose(items.get(2))
                    .open(items.get(3))
                    .high(items.get(4))
                    .low(items.get(5))
                    .close(items.get(6))
                    .volume(items.get(7))
                    .turnover(items.get(8))
                    .build();
        }

    }
}
