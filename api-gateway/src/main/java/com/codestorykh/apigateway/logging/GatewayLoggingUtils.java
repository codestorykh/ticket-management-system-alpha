package com.codestorykh.apigateway.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class GatewayLoggingUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String formatTimestamp() {
        return LocalDateTime.now().format(dateFormatter);
    }

    public static String formatHeaders(HttpHeaders headers, GatewayLoggingProperties properties) {
        if (!properties.isLogHeaders()) {
            return "******";
        }

        Map<String, String> headerMap = new HashMap<>();
        headers.forEach((key, values) -> {
            String value = String.join(", ", values);
            if (properties.isMaskSensitiveHeaders() && 
                properties.getSensitiveHeaders().contains(key.toLowerCase())) {
                value = "******";
            }
            headerMap.put(key, value);
        });

        try {
            return objectMapper.writeValueAsString(headerMap);
        } catch (JsonProcessingException e) {
            return headerMap.toString();
        }
    }

    public static boolean shouldLogBody(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType == null) {
            return true;
        }
        
        return !contentType.includes(MediaType.APPLICATION_OCTET_STREAM) &&
               !contentType.includes(MediaType.IMAGE_JPEG) &&
               !contentType.includes(MediaType.IMAGE_PNG) &&
               !contentType.includes(MediaType.IMAGE_GIF) &&
               !contentType.includes(MediaType.APPLICATION_PDF);
    }

    public static String truncateBody(String body, int maxLength) {
        if (!StringUtils.hasText(body)) {
            return "";
        }
        
        String maskedBody = maskSensitiveFields(body);
        
        if (maskedBody.length() <= maxLength) {
            return maskedBody;
        }
        
        return maskedBody.substring(0, maxLength) + "... [TRUNCATED]";
    }

    public static String maskSensitiveFields(String body) {
        if (!StringUtils.hasText(body)) {
            return body;
        }

        try {
            // Try to parse as JSON and mask sensitive fields
            Object jsonNode = objectMapper.readValue(body, Object.class);
            if (jsonNode instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonMap = (Map<String, Object>) jsonNode;
                maskSensitiveFieldsInMap(jsonMap);
                return objectMapper.writeValueAsString(jsonMap);
            }
        } catch (JsonProcessingException e) {
            // If not valid JSON, return the original body
            log.debug("Body is not valid JSON, returning original: {}", e.getMessage());
        }

        return body;
    }

    @SuppressWarnings("unchecked")
    private static void maskSensitiveFieldsInMap(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof String && isSensitiveField(key)) {
                map.put(key, "***");
            } else if (value instanceof Map) {
                maskSensitiveFieldsInMap((Map<String, Object>) value);
            } else if (value instanceof java.util.List) {
                maskSensitiveFieldsInList((java.util.List<Object>) value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void maskSensitiveFieldsInList(java.util.List<Object> list) {
        for (Object item : list) {
            if (item instanceof Map) {
                maskSensitiveFieldsInMap((Map<String, Object>) item);
            } else if (item instanceof java.util.List) {
                maskSensitiveFieldsInList((java.util.List<Object>) item);
            }
        }
    }

    private static boolean isSensitiveField(String fieldName) {
        String lowerFieldName = fieldName.toLowerCase();
        return lowerFieldName.contains("password") ||
               lowerFieldName.contains("token") ||
               lowerFieldName.contains("secret") ||
               lowerFieldName.contains("key") ||
               lowerFieldName.contains("credential");
    }

    public static String formatDuration(long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        return String.valueOf(duration);
    }

    public static String createLogMessage(String template, Map<String, String> placeholders) {
        String message = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
} 