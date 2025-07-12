package com.codestorykh.user.exception;

import java.io.Serial;

public class BasedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String code;
    private final String message;
    private final String detail;
    private final String field;
    private final String value;


    public BasedException(String code, String message, String detail, String field, String value) {
        this.code = code;
        this.message = message;
        this.detail = detail;
        this.field = field;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
