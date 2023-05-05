package com.phemex.client.service;

import com.phemex.client.domain.market.KlinePushEvent;

import java.util.List;

public interface StepRatioProvider {

    long provide(List<KlinePushEvent.KlineEntry> entryList);
}
