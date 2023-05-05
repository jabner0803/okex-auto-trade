package com.phemex.client.service.ctx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceAnalysisResult {

    private long meanValue;

    private long standardDeviation;

    private long maxValue;

    private long minValue;

}

