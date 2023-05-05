package com.phemex.client.utils;

import com.phemex.client.service.ctx.PriceAnalysisResult;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

public final class ApacheMathLibraryUtils {

    public static PriceAnalysisResult doAnalysis(List<Long> input) {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        input.forEach(descriptiveStatistics::addValue);
        return PriceAnalysisResult.builder()
                .maxValue((long) descriptiveStatistics.getMax())
                .minValue((long) descriptiveStatistics.getMin())
                .meanValue((long) descriptiveStatistics.getMean())
                .standardDeviation((long) descriptiveStatistics.getStandardDeviation())
                .build();
    }
}
