//package com.phemex.client.impl;
//
//import com.phemex.client.service.PhemexClient;
//import com.phemex.client.constant.KlineInterval;
//import com.phemex.client.listener.PhemexMessageListener;
//import com.phemex.client.service.PhemexWebSocketClient;
//import java.time.Duration;
//import java.util.concurrent.TimeUnit;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//@Ignore
//@Slf4j
//public class PhemexWebsocketTest {
//
//    private PhemexClient phemexClient;
//
//    private PhemexWebSocketClient webSocketClient;
//
//    String url = "https://testnet-api.phemex.com";
//
//    static private String testnetApiKey = "eb08be7a-354f-499b-b0dd-6b22756418d1";
//
//    static private String testnetApiSecret = "PoUv9-49yrZa-Be85VDixBRJU9FhFuByhkfKe8b6XhQ0NGJjMzg0Zi0wMGRkLTRjNWItYWIwNi01Zjg3MGE3YmQ1YjM";
//
//    String wsUri = "wss://testnet.phemex.com/ws/";
//
//    @Before
//    public void setup() throws Exception {
//        if(testnetApiKey == null || testnetApiSecret == null) {
//            throw new IllegalStateException("Please update apiKey and apiSecret before testing");
//        }
//
//        this.phemexClient = PhemexClient.builder()
//            .apiKey(testnetApiKey)
//            .apiSecret(testnetApiSecret)
//            .url(url)
//            .connectionTimeout(Duration.ofSeconds(600))
//            .expiryDuration(Duration.ofSeconds(60))
//            .wsUri(wsUri)
//            .messageListener(new PhemexMessageListener() {
//                @Override
//                public void onMessage(String json) {
//                    //ignore
//                    log.info("websocket received msg {}", json);
//                }
//
//                @Override
//                public void onError(Exception ex) {
//                    //ignore
//                    log.info("websocket got error ", ex);
//                }
//            })
//            .build();
//        this.webSocketClient = this.phemexClient.createWebSocketClient();
//        webSocketClient.connectBlocking(5, TimeUnit.SECONDS);
//        webSocketClient.login();
//    }
//
//    @Test
//    public void testSubKline() throws InterruptedException {
//        webSocketClient.subscribeKline("BTCUSD", KlineInterval.MINUTE_1);
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubAccountPositionOrders() throws InterruptedException {
//        this.webSocketClient.subscribeAccountDetails();
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubOrderBook() throws InterruptedException {
//        this.webSocketClient.subscribeOrderBook("BTCUSD");
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubTick() throws InterruptedException {
//        this.webSocketClient.subscribeTick(".BTC");
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubTrade() throws InterruptedException {
//        this.webSocketClient.subscribeTrades("BTCUSD");
//        Thread.sleep(180_000);
//    }
//}
//package com.phemex.client.impl;
//
//import com.phemex.client.service.PhemexClient;
//import com.phemex.client.constant.KlineInterval;
//import com.phemex.client.listener.PhemexMessageListener;
//import com.phemex.client.service.PhemexWebSocketClient;
//import java.time.Duration;
//import java.util.concurrent.TimeUnit;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//
//@Ignore
//@Slf4j
//public class PhemexWebsocketTest {
//
//    private PhemexClient phemexClient;
//
//    private PhemexWebSocketClient webSocketClient;
//
//    String url = "https://testnet-api.phemex.com";
//
//    static private String testnetApiKey = "eb08be7a-354f-499b-b0dd-6b22756418d1";
//
//    static private String testnetApiSecret = "PoUv9-49yrZa-Be85VDixBRJU9FhFuByhkfKe8b6XhQ0NGJjMzg0Zi0wMGRkLTRjNWItYWIwNi01Zjg3MGE3YmQ1YjM";
//
//    String wsUri = "wss://testnet.phemex.com/ws/";
//
//    @Before
//    public void setup() throws Exception {
//        if(testnetApiKey == null || testnetApiSecret == null) {
//            throw new IllegalStateException("Please update apiKey and apiSecret before testing");
//        }
//
//        this.phemexClient = PhemexClient.builder()
//            .apiKey(testnetApiKey)
//            .apiSecret(testnetApiSecret)
//            .url(url)
//            .connectionTimeout(Duration.ofSeconds(600))
//            .expiryDuration(Duration.ofSeconds(60))
//            .wsUri(wsUri)
//            .messageListener(new PhemexMessageListener() {
//                @Override
//                public void onMessage(String json) {
//                    //ignore
//                    log.info("websocket received msg {}", json);
//                }
//
//                @Override
//                public void onError(Exception ex) {
//                    //ignore
//                    log.info("websocket got error ", ex);
//                }
//            })
//            .build();
//        this.webSocketClient = this.phemexClient.createWebSocketClient();
//        webSocketClient.connectBlocking(5, TimeUnit.SECONDS);
//        webSocketClient.login();
//    }
//
//    @Test
//    public void testSubKline() throws InterruptedException {
//        webSocketClient.subscribeKline("BTCUSD", KlineInterval.MINUTE_1);
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubAccountPositionOrders() throws InterruptedException {
//        this.webSocketClient.subscribeAccountDetails();
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubOrderBook() throws InterruptedException {
//        this.webSocketClient.subscribeOrderBook("BTCUSD");
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubTick() throws InterruptedException {
//        this.webSocketClient.subscribeTick(".BTC");
//        Thread.sleep(180_000);
//    }
//
//    @Test
//    public void testSubTrade() throws InterruptedException {
//        this.webSocketClient.subscribeTrades("BTCUSD");
//        Thread.sleep(180_000);
//    }
//}
