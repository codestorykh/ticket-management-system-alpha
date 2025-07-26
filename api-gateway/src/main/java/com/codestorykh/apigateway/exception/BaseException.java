package com.codestorykh.apigateway.exception;

import lombok.Getter;
import lombok.Setter;

public class BaseException extends RuntimeException{

    @Getter
    @Setter
    private String errorCode;

    @Getter
    @Setter
    private String message;

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}
