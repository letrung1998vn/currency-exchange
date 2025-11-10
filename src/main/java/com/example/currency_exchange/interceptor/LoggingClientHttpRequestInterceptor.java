package com.example.currency_exchange.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);
    private static final int MAX_PAYLOAD_LENGTH = 4096;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Log request
        if (log.isInfoEnabled()) {
            String reqBody = body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "<empty>";
            log.info("Outgoing Request -> method={} uri={} headers={} body={}",
                    request.getMethod(), request.getURI(), request.getHeaders(), reqBody);
        }

        ClientHttpResponse response = execution.execute(request, body);

        byte[] respBody = StreamUtils.copyToByteArray(response.getBody());
        String respBodyStr = respBody.length > 0 ? new String(respBody, StandardCharsets.UTF_8) : "<empty>";
        log.info("Incoming Response <- status={} headers={} body={}",
                response.getStatusCode(), response.getHeaders(), respBodyStr);

        return response;
    }

}
