package com.phemex.client.httpops.impl;

import com.phemex.client.constant.PhemexApiConstant;
import com.phemex.client.exceptions.PhemexException;
import com.phemex.client.httpops.HttpOps;
import com.phemex.client.message.PhemexResponse;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpOpsImpl implements HttpOps {

    private final ConnectionProp connectionProp;

    @Override
    public CompletableFuture<String> sendAsync(URI uri, String accessToken, byte[] secretKey, String method, String queryString, long expiry, String body, Duration timeout) {
        int connectionTimeout = (int) Duration.ofSeconds(Integer.parseInt(connectionProp.getTimeoutSeconds())).toMillis();
        CompletableFuture<String> f = new CompletableFuture<>();

        HttpURLConnection conn;
        URI withQueryUri;
        try {
            withQueryUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), queryString, uri.getFragment());
            conn = (HttpURLConnection) withQueryUri.toURL().openConnection();
        } catch (IOException | URISyntaxException e) {
            f.completeExceptionally(e);
            return f;
        }
        conn.setConnectTimeout(connectionTimeout);
        conn.setReadTimeout(connectionTimeout);

        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            f.completeExceptionally(e);
            return f;
        }
        long expry = expiry / 1_000L;

        if (connectionProp.getApiKey() != null) {
            conn.setRequestProperty(PhemexApiConstant.PHEMEX_HEADER_REQUEST_EXPIRY, expry + "");
            conn.setRequestProperty(PhemexApiConstant.PHEMEX_HEADER_REQUEST_ACCESS_TOKEN, connectionProp.getApiKey());
            conn.setRequestProperty(PhemexApiConstant.PHEMEX_HEADER_REQUEST_SIGNATURE,
                    ClientUtils.sign(withQueryUri.getPath(), queryString, expry, body, secretKey));
        }

        if (body != null) {
            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);
            OutputStream os = null;
            try {
                os = conn.getOutputStream();
                os.write(body.getBytes(StandardCharsets.UTF_8));
                os.flush();
            } catch (IOException e) {
                f.completeExceptionally(e);
                return f;
            }
        }

        int respCode;
        try {
            long st = System.currentTimeMillis();
            respCode = conn.getResponseCode();
            //System.out.println("One round trip latency is: \t\t" + (System.currentTimeMillis()-st) );
        } catch (IOException e) {
            f.completeExceptionally(e);
            return f;
        }
        if (respCode != 200) {
            try {
                f.completeExceptionally(toPhemexException(IOUtils.toString(conn.getErrorStream(), StandardCharsets.UTF_8)));
            } catch (IOException e) {
                log.warn("Got exception reading error output stream", e);
            }
            return f;
        }

        try (InputStream is = conn.getInputStream()) {
            f.complete(IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8));
            return f;
        } catch (IOException e) {
            f.completeExceptionally(new PhemexException("", -1, null, e));
            return f;
        }
    }

    private PhemexException toPhemexException(String str) {
        log.debug("Got exception str {}", str);
        try {
            PhemexResponse res = ClientUtils.objectMapper.readValue(str, PhemexResponse.class);
            return new PhemexException(res.getMsg(), res.getCode(), null);
        } catch (IOException ex) {
            log.warn("Failed to translate to phemex response", ex);
        }
        return new PhemexException(str, -1, null);
    }
}
