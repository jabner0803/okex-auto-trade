package com.phemex.client;

import com.phemex.client.config.ConnectionConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Import;

//@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Import({ConnectionConfig.class})
public class PhemexClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhemexClientApplication.class, args).start();
    }
}
