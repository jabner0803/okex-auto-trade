package com.phemex.client.config;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.phemex.client.httpops.HttpOps;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.prop.GrayProp;
import com.phemex.client.service.impl.AccountImpl;
import com.phemex.client.service.quant.KlineBasedQuant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.Executors;

import static com.phemex.client.constant.ClientConstants.phemexClientEventBus;

@Slf4j
@EnableConfigurationProperties({ConnectionProp.class, GrayProp.class})
@ComponentScan(basePackageClasses = {AccountImpl.class, HttpOps.class, KlineBasedQuant.class})
public class ConnectionConfig {

    @Bean(value = phemexClientEventBus)
    public EventBus eventBus() {
        return new AsyncEventBus(phemexClientEventBus, Executors.newSingleThreadExecutor(r -> {
                    Thread thread = new Thread(() -> {
                        r.run();
                    }, "phemex-client-eventbus");
                    thread.setDaemon(true);
                    thread.setUncaughtExceptionHandler((t, ex) -> {
                        log.warn("phemex-client-eventbus {}-{} got uncaught exception ", t.getId(), t.getName(), ex);
                    });
                    return thread;
                }
        ));
    }
}
