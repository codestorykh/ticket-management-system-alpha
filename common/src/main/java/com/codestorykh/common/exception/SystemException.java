package com.codestorykh.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class SystemException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;
    private final transient Object[] args;

    public SystemException(String message) {
        super(message);
        this.errorCode = "SYSTEM_ERROR";
        this.errorMessage = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.args = null;
    }

    public SystemException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.args = null;
    }

    public SystemException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.httpStatus = httpStatus;
        this.args = null;
    }

    public SystemException(String errorCode, String message, HttpStatus httpStatus, Object... args) {
        super(message);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.httpStatus = httpStatus;
        this.args = args;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.errorMessage = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.args = null;
    }

    public SystemException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.args = null;
    }

    public SystemException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.httpStatus = httpStatus;
        this.args = null;
    }

    public SystemException(String errorCode, String message, HttpStatus httpStatus, Throwable cause, Object... args) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
        this.httpStatus = httpStatus;
        this.args = args;
    }

    @Override
    public String toString() {
        return String.format("BusinessException{errorCode='%s', errorMessage='%s', httpStatus=%s}",
                errorCode, errorMessage, httpStatus);
    }
} 