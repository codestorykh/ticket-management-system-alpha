package com.codestorykh.apigateway.filter;

import com.codestorykh.apigateway.entity.RequestLog;
import com.codestorykh.apigateway.logging.GatewayLoggingProperties;
import com.codestorykh.apigateway.logging.GatewayLoggingUtils;
import com.codestorykh.apigateway.service.RequestLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private final GatewayLoggingProperties properties;
    private final RequestLogService requestLogService;

    public LoggingFilter(GatewayLoggingProperties properties, RequestLogService requestLogService) {
        this.properties = properties;
        this.requestLogService = requestLogService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String uri = request.getURI().toString();
        
/*
        // Skip entire logging for sensitive endpoints to avoid any body consumption
        boolean isSensitiveEndpoint = path.contains("/login") || 
                                    path.contains("/auth") ||
                                    path.contains("/registration") ||
                                    path.contains("/public/users") ||
                                    uri.contains("/login") ||
                                    uri.contains("/auth") ||
                                    uri.contains("/registration") ||
                                    uri.contains("/public/users");
        
        if (isSensitiveEndpoint) {
            log.info("Skipping entire logging for sensitive endpoint: {} (URI: {})", path, uri);
            return chain.filter(exchange);
        }
*/

        log.info("Processing non-sensitive endpoint: {} (URI: {})", path, uri);
        long startTime = System.currentTimeMillis();
        String requestId = GatewayLoggingUtils.generateRequestId();
        
        // Get correlation ID from header or generate one if not present
        final String correlationId = getOrGenerateCorrelationId(exchange);
        
        exchange.getAttributes().put("requestId", requestId);
        exchange.getAttributes().put("startTime", startTime);
        exchange.getAttributes().put("correlationId", correlationId);

        ServerHttpResponse response = exchange.getResponse();

        logRequest(request, requestId);

        // Only capture the request body if logging is enabled and the body should be logged
        boolean shouldLogBody = properties.isLogRequestBody() && 
                               GatewayLoggingUtils.shouldLogBody(request.getHeaders());
        
        log.debug("[{}] Path: {}, URI: {}, shouldLogBody: {}, logRequestBody: {}, shouldLogBody: {}", 
                 requestId, path, uri, shouldLogBody, properties.isLogRequestBody(), 
                 GatewayLoggingUtils.shouldLogBody(request.getHeaders()));
        
        if (shouldLogBody) {
            log.debug("[{}] Capturing request body for path: {}", requestId, path);
            return captureRequestBody(exchange, chain, startTime, requestId, correlationId);
        } else {
            log.debug("[{}] Skipping body logging for path: {}", requestId, path);
            // Skip body logging entirely to avoid consuming the body
            return chain.filter(exchange)
                    .doFinally(signalType -> {
                        long duration = System.currentTimeMillis() - startTime;
                        logResponse(response, requestId, duration);
                        // Save the request log asynchronously without blocking the response
                        saveRequestLogAsync(request, response, requestId, correlationId, duration, null);
                    });
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private void logRequest(ServerHttpRequest request, String requestId) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("method", request.getMethod().name());
        placeholders.put("uri", request.getURI().toString());
        placeholders.put("headers", GatewayLoggingUtils.formatHeaders(request.getHeaders(), properties));
        placeholders.put("body", "******");
        placeholders.put("duration", "0");
        placeholders.put("requestId", requestId);
        placeholders.put("timestamp", GatewayLoggingUtils.formatTimestamp());

        String logMessage = GatewayLoggingUtils.createLogMessage(properties.getRequestLogFormat(), placeholders);
        log.info("[{}] {}", requestId, logMessage);
    }

    private void logResponse(ServerHttpResponse response, String requestId, long duration) {
        var status = response.getStatusCode();
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("status", status != null ? status.toString() : "UNKNOWN");
        placeholders.put("headers", GatewayLoggingUtils.formatHeaders(response.getHeaders(), properties));
        placeholders.put("body", "******");
        placeholders.put("duration", String.valueOf(duration));
        placeholders.put("requestId", requestId);
        placeholders.put("timestamp", GatewayLoggingUtils.formatTimestamp());

        String logMessage = GatewayLoggingUtils.createLogMessage(properties.getResponseLogFormat(), placeholders);

        if (status != null) {
            if (status.is2xxSuccessful()) {
                log.info("[{}] {}", requestId, logMessage);
            } else if (status.is4xxClientError()) {
                log.warn("[{}] {}", requestId, logMessage);
            } else if (status.is5xxServerError()) {
                log.error("[{}] {}", requestId, logMessage);
            } else {
                log.info("[{}] {}", requestId, logMessage);
            }
        } else {
            log.info("[{}] {}", requestId, logMessage);
        }
    }

    private Mono<Void> captureRequestBody(ServerWebExchange exchange, GatewayFilterChain chain, 
                                         long startTime, String requestId, String correlationId) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .defaultIfEmpty("")
                .flatMap(requestBody -> {
                    logRequestWithBody(exchange.getRequest(), requestId, requestBody);
                    return chain.filter(exchange)
                            .doFinally(signalType -> {
                                long duration = System.currentTimeMillis() - startTime;
                                logResponse(exchange.getResponse(), requestId, duration);
                                saveRequestLogAsync(exchange.getRequest(), exchange.getResponse(), requestId, correlationId, duration, requestBody);
                            });
                });
    }

    private void logRequestWithBody(ServerHttpRequest request, String requestId, String requestBody) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("method", request.getMethod().name());
        placeholders.put("uri", request.getURI().toString());
        placeholders.put("headers", GatewayLoggingUtils.formatHeaders(request.getHeaders(), properties));
        placeholders.put("body", GatewayLoggingUtils.truncateBody(requestBody, properties.getMaxBodyLength()));
        placeholders.put("duration", "0");
        placeholders.put("requestId", requestId);
        placeholders.put("timestamp", GatewayLoggingUtils.formatTimestamp());

        String logMessage = GatewayLoggingUtils.createLogMessage(properties.getRequestLogFormat(), placeholders);
        log.info("[{}] {}", requestId, logMessage);
    }



    private void saveRequestLogAsync(ServerHttpRequest request, ServerHttpResponse response, 
                                    String requestId, String correlationId, long duration, String requestBody) {
        // Run the database save operation asynchronously to avoid blocking the response
        Mono.fromRunnable(() -> {
            try {
                RequestLog requestLog = RequestLog.builder()
                        .requestId(requestId)
                        .correlationId(correlationId)
                        .method(request.getMethod().name())
                        .uri(request.getURI().toString())
                        .path(request.getPath().value())
                        .clientIp(getClientIP(request))
                        .userAgent(request.getHeaders().getFirst("User-Agent"))
                        .requestHeaders(GatewayLoggingUtils.formatHeaders(request.getHeaders(), properties))
                        .requestBody(requestBody != null ? GatewayLoggingUtils.truncateBody(requestBody, properties.getMaxBodyLength()) : null)
                        .requestSize(request.getHeaders().getContentLength())
                        .responseStatus(response.getStatusCode() != null ? response.getStatusCode().value() : null)
                        .responseHeaders(GatewayLoggingUtils.formatHeaders(response.getHeaders(), properties))
                        .durationMs(duration)
                        .serviceName(properties.getServiceName())
                        .build();

                requestLogService.save(requestLog)
                        .subscribe(
                                saved -> log.debug("Saved request log with ID: {}", saved.getId()),
                                error -> log.error("Failed to save request log: {}", error.getMessage())
                        );
            } catch (Exception e) {
                log.error("Error creating request log: {}", e.getMessage());
            }
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic()).subscribe();
    }

    private String getOrGenerateCorrelationId(ServerWebExchange exchange) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(properties.getCorrelationIdHeader());
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = GatewayLoggingUtils.generateRequestId(); // Generate a new correlation ID
        }
        return correlationId;
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