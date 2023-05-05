package com.phemex.client.listener.impl;

import com.google.common.eventbus.EventBus;
import com.phemex.client.constant.PhemexServerEventType;
import com.phemex.client.domain.market.response.ExchangeBaseResponse;
import com.phemex.client.listener.PhemexMessageListener;
import com.phemex.client.utils.PhemexServerEventParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.phemex.client.constant.ClientConstants.phemexClientEventBus;

@Slf4j
@Service
public class PhemexMessageListenerImpl implements PhemexMessageListener, SmartInitializingSingleton {

    @Autowired
    @Qualifier(value = phemexClientEventBus)
    private EventBus asyncBus;

    @Override
    public void onMessage(String json) {
        log.debug("websocket received msg {}", json);
        ExchangeBaseResponse response = PhemexServerEventParser.INSTANCE.text2ExchangeResponse(json);
        if (response.getEventType() != PhemexServerEventType.REPLY) {
            asyncBus.post(response.getData());
        }
        if (response.getEventType() == PhemexServerEventType.ACCOUNT_ORDER_EVENT) {
            log.info("ACCOUNT_ORDER_EVENT {}", json);
        }
    }

    @Override
    public void onError(Exception ex) {
        log.info("websocket got error ", ex);

    }

    @Override
    public void afterSingletonsInstantiated() {

    }
}
