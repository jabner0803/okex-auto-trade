package com.phemex.client.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "phemex.client.connect")
public class ConnectionProp {

    private String webUrl;

    private String wsUrl;

    private String timeoutSeconds;

    private long expirySeconds;

    private String apiKey;

    private String apiSecret;
}
