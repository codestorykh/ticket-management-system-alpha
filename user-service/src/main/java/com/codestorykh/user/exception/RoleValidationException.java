package com.codestorykh.user.exception;

import lombok.Getter;

public class RoleValidationException extends RuntimeException {

    @Getter
    private final String field;
    @Getter
    private final String value;

    public RoleValidationException(String field, String value) {
        super(String.format("%s is invalid", field));
        this.field = field;
        this.value = value;
    }
}
