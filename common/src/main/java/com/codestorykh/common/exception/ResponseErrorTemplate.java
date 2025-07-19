package com.codestorykh.common.exception;

import com.codestorykh.common.dto.Metadata;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import java.util.HashMap;

public record ResponseErrorTemplate(
        @JsonProperty("message") String message,
        @JsonProperty("code") String code,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("data") Object data,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("metadata") Metadata metadata,
        @JsonIgnore boolean isError,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonProperty("errors") Map<String, String> errors) {

    public ResponseErrorTemplate(String message, String code, Object data, boolean isError) {
        this(message, code, data, null, isError, new HashMap<>());
    }

    public ResponseErrorTemplate(String message, String code, Object data, Metadata metadata, boolean isError) {
        this(message, code, data, metadata, isError, new HashMap<>());
    }

    public ResponseErrorTemplate withError(String field, String errorMessage) {
        Map<String, String> newErrors = new HashMap<>(errors);
        newErrors.put(field, errorMessage);
        return new ResponseErrorTemplate(message, code, data, metadata, isError, newErrors);
    }

    public ResponseErrorTemplate withErrors(Map<String, String> additionalErrors) {
        Map<String, String> newErrors = new HashMap<>(errors);
        newErrors.putAll(additionalErrors);
        return new ResponseErrorTemplate(message, code, data, metadata, isError, newErrors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String getErrorForField(String field) {
        return errors.get(field);
    }
}