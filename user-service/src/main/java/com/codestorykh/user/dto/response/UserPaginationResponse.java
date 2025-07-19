package com.codestorykh.user.dto.response;

import com.codestorykh.common.dto.PaginationResponse;
import com.codestorykh.common.dto.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record UserPaginationResponse(
        @JsonProperty("items") List<UserResponse> userResponses,
        @JsonProperty("page") PaginationResponse paginationResponse,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("metadata") Map<String, Object> metadata) {

    public UserPaginationResponse(List<UserResponse> userResponses, PaginationResponse paginationResponse) {
        this(userResponses, paginationResponse, null);
    }

    public UserPaginationResponse withMetadata(Map<String, Object> metadata) {
        return new UserPaginationResponse(userResponses, paginationResponse, metadata);
    }

    public UserPaginationResponse withMetadata(String key, Object value) {
        Map<String, Object> newMetadata = metadata != null ? new java.util.HashMap<>(metadata) : new java.util.HashMap<>();
        newMetadata.put(key, value);
        return new UserPaginationResponse(userResponses, paginationResponse, newMetadata);
    }
}
