package com.codestorykh.user.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {

    private boolean success;
    private String message;
    private String errorCode;
    private String field;
    private String value;
    private LocalDateTime timestamp;
    private Map<String, String> details;

    public ErrorResponse() {
        this.success = false;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String errorCode, String message) {
        this();
        this.errorCode = errorCode;
        this.message = message;
    }
    public ErrorResponse(String errorCode, String message, String field, String value) {
        this(errorCode, message);
        this.field = field;
        this.value = value;
    }

}
