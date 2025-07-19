package com.codestorykh.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoleResponse(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        @JsonProperty("created_by") String createdBy,
        @JsonProperty("created_on") String createdOn,
        @JsonProperty("updated_by") String updatedBy,
        @JsonProperty("updated_on") String updatedOn
) {
}
