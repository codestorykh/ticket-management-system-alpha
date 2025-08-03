package com.codestorykh.apigateway.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "gateway.logging")
public class GatewayLoggingProperties {

    private boolean enabled = true;

    private String level = "INFO";

    private boolean logRequestBody = false;

    private boolean logResponseBody = false;

    private boolean logHeaders = true;

    private boolean maskSensitiveHeaders = true;

    private List<String> sensitiveHeaders = Arrays.asList(
            "authorization",
            "x-api-key",
            "x-auth-token",
            "cookie",
            "set-cookie",
            "x-forwarded-for",
            "x-real-ip"
    );

    private List<String> sensitiveFields = Arrays.asList(
            "password",
            "token",
            "secret",
            "key",
            "credential"
    );

    private int maxBodyLength = 1000;

    private boolean logPerformance = true;

    private boolean logErrorStackTraces = true;

    private boolean includeRequestId = true;

    private String requestLogFormat = "REQUEST: {method} {uri} - Headers: {headers} - Body: {body} - Duration: {duration}ms - Timestamp: {timestamp}";

    private String responseLogFormat = "RESPONSE: {status} - Headers: {headers} - Body: {body} - Duration: {duration}ms - Timestamp: {timestamp}";

    private String errorLogFormat = "ERROR: {method} {uri} - Error: {error} - Duration: {duration}ms - Timestamp: {timestamp}";

    private boolean logRequestMetrics = true;

    private List<String> excludedPaths = Arrays.asList(
            "/health",
            "/actuator/health",
            "/actuator/info"
    );

    private boolean enableCorrelationId = true;

    private String correlationIdHeader = "X-Correlation-ID";

    private boolean logUserAgent = true;

    private boolean logClientIP = true;

    private boolean logRequestSize = true;

    private boolean logResponseSize = true;

    private String serviceName = "api-gateway";
} 