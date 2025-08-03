package com.codestorykh.apigateway.filter;

import com.codestorykh.apigateway.logging.GatewayLoggingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RequestMetricsFilter implements GlobalFilter, Ordered {

    private final GatewayLoggingProperties properties;

    public RequestMetricsFilter(GatewayLoggingProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled() || !properties.isLogRequestMetrics()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String requestId = exchange.getAttribute("requestId");
        
        if (requestId != null) {
            logRequestMetrics(request, requestId);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 3;
    }

    private void logRequestMetrics(ServerHttpRequest request, String requestId) {
        Map<String, String> metrics = new HashMap<>();
        
        if (properties.isLogClientIP()) {
            String clientIP = getClientIP(request);
            metrics.put("clientIP", clientIP);
        }
        
        if (properties.isLogUserAgent()) {
            String userAgent = request.getHeaders().getFirst("User-Agent");
            metrics.put("userAgent", userAgent != null ? userAgent : "Unknown");
        }
        
        if (properties.isLogRequestSize()) {
            long contentLength = request.getHeaders().getContentLength();
            metrics.put("requestSize", contentLength > 0 ? String.valueOf(contentLength) : "Unknown");
        }

        if (!metrics.isEmpty()) {
            String metricsLog = String.format("REQUEST METRICS: %s", metrics);
            log.debug("[{}] {}", requestId, metricsLog);
        }
    }

    private String getClientIP(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "Unknown";
    }
} 