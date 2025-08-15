package com.codestorykh.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TokenVerificationResponse {
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("data")
    private Map<String, Object> data;
    
    @JsonProperty("isError")
    private boolean isError;
    
    public boolean isValid() {
        return !isError && "TOKEN_VALID".equals(code);
    }
} 