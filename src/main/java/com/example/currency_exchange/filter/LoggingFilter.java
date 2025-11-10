package com.example.currency_exchange.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 4096;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // wrap request/response to cache payloads
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        }
        finally {
            logRequest(wrappedRequest);
            logResponse(wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString();
        String headers = getHeaders(request);
        String payload = getRequestPayload(request);

        logger.info("Incoming request: method={} path={} query={} headers={} payload={}",
                method, path, query, headers, payload);
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        int status = response.getStatus();
        String payload = getResponsePayload(response);

        logger.info("Outgoing response: status={} payload={}", status, payload);
    }

    private String getHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return "";
        }
        return Collections.list(headerNames).stream()
                .map(name -> name + "=" +
                             Collections.list(request.getHeaders(name)).stream().collect(Collectors.joining(",")))
                .collect(Collectors.joining("; "));
    }

    private String getRequestPayload(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf == null || buf.length == 0) {
            return "";
        }
        int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
        String payload = new String(buf, 0, length, getCharset(request.getCharacterEncoding()));
        if (buf.length > MAX_PAYLOAD_LENGTH) {
            payload += "...(truncated " + buf.length + " bytes)";
        }
        return payload;
    }

    private String getResponsePayload(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf == null || buf.length == 0) {
            return "";
        }
        int length = Math.min(buf.length, MAX_PAYLOAD_LENGTH);
        String payload = new String(buf, 0, length, getCharset(response.getCharacterEncoding()));
        if (buf.length > MAX_PAYLOAD_LENGTH) {
            payload += "...(truncated " + buf.length + " bytes)";
        }
        return payload;
    }

    private java.nio.charset.Charset getCharset(String encoding) {
        try {
            return encoding == null ? StandardCharsets.UTF_8 : java.nio.charset.Charset.forName(encoding);
        }
        catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }
}
