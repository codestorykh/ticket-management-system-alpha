package com.codestorykh.apigateway.filter;

import com.codestorykh.apigateway.dto.TokenVerificationResponse;
import com.codestorykh.apigateway.exception.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    // Paths that don't require authentication
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/public/users/login",
        "/api/public/users/registration",
        "/api/public/users/refreshToken",
        "/api/public/users/logout",
        "/api/public/users/verify-token",
        "/health",
        "/actuator/health",
        "/actuator/info"
    );

    public JwtAuthenticationFilter(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // Skip authentication for public paths
        if (isPublicPath(path)) {
            log.debug("Skipping authentication for public path: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return handleUnauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        
        return verifyToken(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        log.debug("Token verification successful for path: {}", path);
                        return chain.filter(exchange);
                    } else {
                        log.warn("Token verification failed for path: {}", path);
                        return handleUnauthorized(exchange, "Invalid token");
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("Error during token verification for path {}: {}", path, throwable.getMessage());
                    return handleUnauthorized(exchange, "Token verification error");
                });
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Boolean> verifyToken(String token) {
        return webClient.post()
                .uri(userServiceUrl + "/api/public/users/verify-token")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(TokenVerificationResponse.class)
                .map(response -> {
                    log.debug("Token verification response: {}", response);
                    return response.isValid();
                })
                .onErrorResume(throwable -> {
                    log.error("Error calling user service for token verification: {}", throwable.getMessage());
                    return Mono.just(false);
                });
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Object> errorResponse = ApiResponse.error("UNAUTHORIZED", message);

        try {
            String errorJson = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error creating error response: {}", e.getMessage());
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10; // Run after CorrelationIdFilter but before other filters
    }
} 