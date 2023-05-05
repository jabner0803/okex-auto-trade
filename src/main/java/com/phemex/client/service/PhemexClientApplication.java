//package com.phemex.client.service;
//
//import com.phemex.client.constant.OrdType;
//import com.phemex.client.constant.PlatformType;
//import com.phemex.client.constant.QtyType;
//import com.phemex.client.constant.Side;
//import com.phemex.client.constant.TimeInForce;
//import com.phemex.client.constant.TriggerType;
//import com.phemex.client.domain.OrderModelVo;
//import com.phemex.client.listener.PhemexMessageListener;
//import com.phemex.client.message.CreateOrderRequest;
//import com.phemex.client.message.CreateSpotOrderRequest;
//import com.phemex.client.message.PhemexResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.time.Duration;
//import java.util.UUID;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//@Slf4j
//@SpringBootApplication
//public class PhemexClientApplication {
//    private PhemexClient phemexClient;
//
////    String url = "https://testnet-api.phemex.com";
//
//    String url = "https://fat3-api.phemex.com";
//
//    static private String testnetApiKey = "eb08be7a-354f-499b-b0dd-6b22756418d1";
//
//    static private String testnetApiSecret = "PoUv9-49yrZa-Be85VDixBRJU9FhFuByhkfKe8b6XhQ0NGJjMzg0Zi0wMGRkLTRjNWItYWIwNi01Zjg3MGE3YmQ1YjM";
//
//    String wsUri = "wss://testnet.phemex.com/ws/";
//
//    private volatile boolean finished = false;
//
//    private ExecutorService executorService;
//
//    public PhemexClientApplication() {
//        init();
//    }
//
//    private void init() {
//        if(testnetApiKey == null || testnetApiSecret == null) {
//            throw new IllegalStateException("Please update apiKey and apiSecret before testing");
//        }
//
//        this.phemexClient = PhemexClient.builder()
//                .apiKey(testnetApiKey)
//                .apiSecret(testnetApiSecret)
//                .url(url)
//                .connectionTimeout(Duration.ofSeconds(600))
//                .expiryDuration(Duration.ofSeconds(60))
//                .wsUri(wsUri)
//                .messageListener(new PhemexMessageListener() {
//                    @Override
//                    public void onMessage(String json) {
//                        //ignore
//                        log.info("websocket received msg {}", json);
//                    }
//
//                    @Override
//                    public void onError(Exception ex) {
//                        //ignore
//                        log.info("websocket got error ", ex);
//                    }
//                })
//                .build();
//
//        executorService = Executors.newFixedThreadPool(10);
//    }
//
//    private void createContractOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType orderType) throws ExecutionException, InterruptedException, TimeoutException {
//        PhemexResponse<OrderModelVo> res = this.phemexClient.orders().createOrder(CreateOrderRequest.builder()
//                .symbol(symbol)
//                .clOrdID(UUID.randomUUID().toString())
//                .orderQty(orderQty)
//                .priceEp(orderPrice)
//                .side(side)
//                .build()).get(5, TimeUnit.SECONDS);
//        log.info("Limit Order created {}", res.getData());
//    }
//
//    private void createSpotOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType ordType) {
//        while (!finished) {
//            PhemexResponse<OrderModelVo> res = null;
//            try {
//                CreateSpotOrderRequest spotOrder = CreateSpotOrderRequest.builder()
//                        .symbol(symbol)
//                        .clOrdID(UUID.randomUUID().toString())
//                        .quoteQtyEv(orderQty)
//                        .baseQtyEv(0L)
//                        .stopPxEp(0L)
//                        .priceEp(orderPrice)
//                        .side(side)
//                        .ordType(ordType)
//                        .qtyType(QtyType.ByQuote)
//                        .trigger(TriggerType.ByMarkPrice)
//                        .platform(PlatformType.API)
//                        .timeInForce(TimeInForce.GoodTillCancel)
//                        .text("")
//                        .build();
//                res = this.phemexClient.orders().createSpotOrder(spotOrder).get(5, TimeUnit.SECONDS);
//                finished = true;
//            } catch (Exception e) {
//                log.error("Exception {}", e);
//            }
//            log.info("Limit Order created {}", res.getData());
//        }
//    }
//
//    public static void main(String[] args) {
//        PhemexClientApplication application = new PhemexClientApplication();
//        application.createSpotOrder("sBTCUSDT", 1000000000L, 3002000000000L, Side.Buy, OrdType.Limit);
//    }
//}
//package com.phemex.client.service;
//
//import com.phemex.client.constant.OrdType;
//import com.phemex.client.constant.PlatformType;
//import com.phemex.client.constant.QtyType;
//import com.phemex.client.constant.Side;
//import com.phemex.client.constant.TimeInForce;
//import com.phemex.client.constant.TriggerType;
//import com.phemex.client.domain.OrderModelVo;
//import com.phemex.client.listener.PhemexMessageListener;
//import com.phemex.client.message.CreateOrderRequest;
//import com.phemex.client.message.CreateSpotOrderRequest;
//import com.phemex.client.message.PhemexResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.time.Duration;
//import java.util.UUID;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//@Slf4j
//@SpringBootApplication
//public class PhemexClientApplication {
//    private PhemexClient phemexClient;
//
////    String url = "https://testnet-api.phemex.com";
//
//    String url = "https://fat3-api.phemex.com";
//
//    static private String testnetApiKey = "eb08be7a-354f-499b-b0dd-6b22756418d1";
//
//    static private String testnetApiSecret = "PoUv9-49yrZa-Be85VDixBRJU9FhFuByhkfKe8b6XhQ0NGJjMzg0Zi0wMGRkLTRjNWItYWIwNi01Zjg3MGE3YmQ1YjM";
//
//    String wsUri = "wss://testnet.phemex.com/ws/";
//
//    private volatile boolean finished = false;
//
//    private ExecutorService executorService;
//
//    public PhemexClientApplication() {
//        init();
//    }
//
//    private void init() {
//        if(testnetApiKey == null || testnetApiSecret == null) {
//            throw new IllegalStateException("Please update apiKey and apiSecret before testing");
//        }
//
//        this.phemexClient = PhemexClient.builder()
//                .apiKey(testnetApiKey)
//                .apiSecret(testnetApiSecret)
//                .url(url)
//                .connectionTimeout(Duration.ofSeconds(600))
//                .expiryDuration(Duration.ofSeconds(60))
//                .wsUri(wsUri)
//                .messageListener(new PhemexMessageListener() {
//                    @Override
//                    public void onMessage(String json) {
//                        //ignore
//                        log.info("websocket received msg {}", json);
//                    }
//
//                    @Override
//                    public void onError(Exception ex) {
//                        //ignore
//                        log.info("websocket got error ", ex);
//                    }
//                })
//                .build();
//
//        executorService = Executors.newFixedThreadPool(10);
//    }
//
//    private void createContractOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType orderType) throws ExecutionException, InterruptedException, TimeoutException {
//        PhemexResponse<OrderModelVo> res = this.phemexClient.orders().createOrder(CreateOrderRequest.builder()
//                .symbol(symbol)
//                .clOrdID(UUID.randomUUID().toString())
//                .orderQty(orderQty)
//                .priceEp(orderPrice)
//                .side(side)
//                .build()).get(5, TimeUnit.SECONDS);
//        log.info("Limit Order created {}", res.getData());
//    }
//
//    private void createSpotOrder(String symbol, long orderQty, long orderPrice, Side side, OrdType ordType) {
//        while (!finished) {
//            PhemexResponse<OrderModelVo> res = null;
//            try {
//                CreateSpotOrderRequest spotOrder = CreateSpotOrderRequest.builder()
//                        .symbol(symbol)
//                        .clOrdID(UUID.randomUUID().toString())
//                        .quoteQtyEv(orderQty)
//                        .baseQtyEv(0L)
//                        .stopPxEp(0L)
//                        .priceEp(orderPrice)
//                        .side(side)
//                        .ordType(ordType)
//                        .qtyType(QtyType.ByQuote)
//                        .trigger(TriggerType.ByMarkPrice)
//                        .platform(PlatformType.API)
//                        .timeInForce(TimeInForce.GoodTillCancel)
//                        .text("")
//                        .build();
//                res = this.phemexClient.orders().createSpotOrder(spotOrder).get(5, TimeUnit.SECONDS);
//                finished = true;
//            } catch (Exception e) {
//                log.error("Exception {}", e);
//            }
//            log.info("Limit Order created {}", res.getData());
//        }
//    }
//
//    public static void main(String[] args) {
//        PhemexClientApplication application = new PhemexClientApplication();
//        application.createSpotOrder("sBTCUSDT", 1000000000L, 3002000000000L, Side.Buy, OrdType.Limit);
//    }
//}
