package com.codestorykh.common.dto;


import java.time.LocalDateTime;
import java.util.Map;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public ApiResponse() {
    }
    
    public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(true, "Success", data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(false, message, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<T>(false, message, null, LocalDateTime.now());
    }

    // Manual getters and setters since Lombok is not working
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }
} 