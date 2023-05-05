package com.phemex.client.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phemex.client.constant.PhemexServerEventType;
import com.phemex.client.domain.market.KlinePushEvent;
import com.phemex.client.domain.market.OrderbookEvent;
import com.phemex.client.domain.market.SymbolEvent;
import com.phemex.client.domain.market.TradePushEvent;
import com.phemex.client.domain.market.response.ExchangeBaseResponse;
import com.phemex.client.domain.market.response.ExchangeReplyResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class PhemexServerEventParser {

    static final public PhemexServerEventParser INSTANCE = defaultInstance();

    private static PhemexServerEventParser defaultInstance() {
        return new PhemexServerEventParser();
    }

    @Getter
    final private ObjectMapper objectMapper = new ObjectMapper();

    public PhemexServerEventParser() {
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * two types of messages 1. request-reply 2. push event
     */
    public ExchangeBaseResponse text2ExchangeResponse(String text) {
        try {
            log.debug("Received text size is {}", text.length());
            JsonNode jsonNode = this.objectMapper.readTree(text);
            if (jsonNode.has("id")) { // reply
                log.debug("{} is a reply ", text);
                ExchangeReplyResponse reply = this.objectMapper.convertValue(jsonNode, ExchangeReplyResponse.class);
                return ExchangeBaseResponse.builder()
                    .data(reply)
                    .eventType(PhemexServerEventType.REPLY)
                    .build();

            } else { // push event
                String fieldName = jsonNode.fieldNames().next();
//                jsonNode.has()
                PhemexServerEventType pushEventType = PhemexServerEventType.toPushType(jsonNode::has);
                log.debug("{} is a push event", pushEventType);
                Object t = this.objectMapper.convertValue(pushEventType.isIncludeRoot() ? jsonNode : jsonNode.get(pushEventType.getTag()), pushEventType.getClazz());
                return ExchangeBaseResponse.builder()
                    .data(t)
                    .eventType(pushEventType)
                    .build();
            }
        } catch (IOException ex) {
            log.error("Failed to parse message {}", text, ex);
            return null;
        }
    }

    public String extractSubIdFromExchangeResponse(ExchangeBaseResponse data) {
        PhemexServerEventType eventType = data.getEventType();
        switch (eventType) {
            case REPLY:
                return String.valueOf(((ExchangeReplyResponse) data.getData()).getId());
            case TICK_EVENT:
                return eventType.extractKey((SymbolEvent) data.getData());
            case KLINE_EVENT:
                return eventType.extractKey((KlinePushEvent) data.getData());
            case TRADE_EVENT:
                return eventType.extractKey((TradePushEvent) data.getData());
            case ORDER_BOOK_EVENT:
                log.debug("extract sub id from event {}", data);
                return eventType.extractKey((OrderbookEvent) data.getData());
            default:
                throw new IllegalStateException("Failed to get subid of event type " + eventType);
        }
    }
}
