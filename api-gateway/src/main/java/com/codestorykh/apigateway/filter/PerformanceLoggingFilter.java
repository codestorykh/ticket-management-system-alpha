package com.codestorykh.apigateway.filter;

import com.codestorykh.apigateway.logging.GatewayLoggingProperties;
import com.codestorykh.apigateway.logging.GatewayLoggingUtils;
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
public class PerformanceLoggingFilter implements GlobalFilter, Ordered {

    private final GatewayLoggingProperties properties;

    public PerformanceLoggingFilter(GatewayLoggingProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isLogPerformance()) {
            return chain.filter(exchange);
        }

        long startTime = System.currentTimeMillis();
        String requestId = exchange.getAttribute("requestId");
        
        if (requestId == null) {
            requestId = GatewayLoggingUtils.generateRequestId();
            exchange.getAttributes().put("requestId", requestId);
        }
        
        exchange.getAttributes().put("startTime", startTime);
        final String finalRequestId = requestId;

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logPerformanceMetrics(exchange, finalRequestId, duration);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void logPerformanceMetrics(ServerWebExchange exchange, String requestId, long duration) {
        ServerHttpRequest request = exchange.getRequest();
        var status = exchange.getResponse().getStatusCode();

        String logMessage = String.format("PERFORMANCE: %s %s - Status: %s - Duration: %dms", 
                request.getMethod().name(),
                request.getURI(),
                status != null ? status.toString() : "UNKNOWN", 
                duration);

        if (duration > 1000) {
            log.warn("[{}] SLOW REQUEST: {}", requestId, logMessage);
        } else if (duration > 500) {
            log.info("[{}] {}", requestId, logMessage);
        } else {
            log.debug("[{}] {}", requestId, logMessage);
        }
    }
} 