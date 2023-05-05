package com.phemex.client.service.impl;


import com.phemex.client.constant.KlineInterval;
import com.phemex.client.listener.PhemexMessageListener;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.service.PhemexClient;
import com.phemex.client.service.PhemexWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@EnableScheduling
public class PhemexWebSocketClientImpl extends WebSocketClient implements PhemexWebSocketClient {

    static AtomicLong idGenerator = new AtomicLong();

    private PhemexMessageListener listener;
    private PhemexClient client;
    private ConnectionProp connectionProp;

    public PhemexWebSocketClientImpl(PhemexClient client, ConnectionProp connectionProp, PhemexMessageListener listener) {
        super(URI.create(connectionProp.getWsUrl()), new Draft_6455());
        this.connectionProp = connectionProp;
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void onMessage(String message) {
        log.debug("Receive Message：" + message);
        this.listener.onMessage(message);
    }

    @Override
    public void onMessage(ByteBuffer blob) {
        String json = new String(blob.array());
        log.debug("Receive Message：" + blob);
        this.listener.onMessage(json);

    }

    @Override
    public void onError(Exception ex) {
        this.listener.onError(ex);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        this.send(handshake.getHttpStatusMessage());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("Closed: code {}, reason {}", code, reason);
    }

    @Override
    public void login() {
        long expiry = Instant.now().plusSeconds(120).getEpochSecond();
        String apiAuth = "{\"method\": \"user.auth\", \"params\": [\"API\", \"" +
                client.apiKey() + "\", \"" + client.apiKeySignature(expiry) + "\", " + expiry + "], \"id\": 1234}";
        this.send(apiAuth);
    }

    @Override
    public void privateLogin() {
        long expiry = Instant.now().plusSeconds(1200).getEpochSecond();
        String apiAuth = "{\"method\": \"user.auth\", \"params\": [\"API\", \"" +
                client.apiKey() + "\", \"" + client.apiSecret() + "\", " + expiry + "], \"id\": 1234}";
        this.send(apiAuth);
    }

    @Override
    public void sendHeartBeat() {
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"server.ping\", \"params\": []}");
    }

    @Override
    public void subscribeAccountDetails() {
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"aop.subscribe\", \"params\": []}");
    }

    @Override
    public void subscribeGAccountDetails() {
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"aop_p.subscribe\", \"params\": []}");
    }

    @Override
    public void subscribeTrades(String symbol) {
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"trade.subscribe\", \"params\":[\"" + Objects.requireNonNull(symbol) + "\"]}");
    }

    @Override
    public void subscribeGTrades(String symbol) {
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"trade_p.subscribe\", \"params\":[\"" + Objects.requireNonNull(symbol) + "\"]}");
    }
    @Override
    public void subscribeKline(String symbol, KlineInterval interval) {
        String str = "{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"kline.subscribe\", \"params\":[\"" + symbol + "\"," + interval.getInterval() + "]}";
        log.debug("sub str {}", str);
        this.send(str);
    }

    @Override
    public void subscribeTick(String symbol) {
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\":\"tick.subscribe\"," + "\"params\": [\"" + Objects.requireNonNull(symbol) + "\"]}");
    }

    @Override
    public void subscribeOrderBook(String... symbols) {
        StringBuilder sb = new StringBuilder();
        for (String s : symbols) {
            sb.append("\"" + s + "\" ,");
        }
        sb.deleteCharAt(sb.length() - 1);
        String sym = sb.toString();
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"orderbook.subscribe\", \"params\": [" + sym + "]}");
    }

    @Override
    public void subscribeGOrderBook(String... symbols) {
        StringBuilder sb = new StringBuilder();
        for (String s : symbols) {
            sb.append("\"" + s + "\" ,");
        }
        sb.deleteCharAt(sb.length() - 1);
        String sym = sb.toString();
        this.send("{\"id\":" + idGenerator.incrementAndGet() + ", \"method\": \"orderbook_p.subscribe\", \"params\": [" + sym + "]}");
    }

}