package com.phemex.client.service.impl;

import com.phemex.client.domain.market.KlinePushEvent;
import com.phemex.client.service.StepRatioProvider;

import java.util.List;

public class KlineBasedStepRatioProvider implements StepRatioProvider {

    @Override
    public long provide(List<KlinePushEvent.KlineEntry> entryList) {
        return 0;
    }
}
