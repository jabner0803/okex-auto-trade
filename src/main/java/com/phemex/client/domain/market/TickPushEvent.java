package com.phemex.client.domain.market;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName("tick")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TickPushEvent implements SymbolEvent {

    private long last;

    private int scale;

    private String symbol;

    private long timestamp;

}
