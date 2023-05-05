package com.phemex.client.service;

import com.phemex.client.constant.KlineInterval;
import org.java_websocket.WebSocket;

import java.util.concurrent.TimeUnit;

public interface PhemexWebSocketClient extends WebSocket {

    boolean connectBlocking(long timeout, TimeUnit timeUnit) throws InterruptedException;

    void closeBlocking() throws InterruptedException;

    void connect();

    void reconnect();

    boolean connectBlocking() throws InterruptedException;

    void login();

    void privateLogin();

    void subscribeAccountDetails();

    void subscribeGAccountDetails();

    void subscribeTrades(String symbol);

    void subscribeGTrades(String symbol);

    void subscribeKline(String symbol, KlineInterval interval);

    void subscribeTick(String symbol);

    void subscribeOrderBook(String... symbols);

    void sendHeartBeat();

    void subscribeGOrderBook(String... symbols);
}
