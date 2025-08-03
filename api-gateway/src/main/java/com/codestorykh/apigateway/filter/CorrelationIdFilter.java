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

@Slf4j
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private final GatewayLoggingProperties properties;

    public CorrelationIdFilter(GatewayLoggingProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled() || !properties.isEnableCorrelationId()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String correlationId = getCorrelationId(request);
        
        exchange.getAttributes().put("correlationId", correlationId);
        
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(properties.getCorrelationIdHeader(), correlationId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    private String getCorrelationId(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(properties.getCorrelationIdHeader());
        
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = GatewayLoggingUtils.generateRequestId();
        }
        
        return correlationId;
    }
} 