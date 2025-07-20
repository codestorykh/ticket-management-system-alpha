package com.codestorykh.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @JsonProperty("id") Long id,

        @JsonProperty("name") 
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @JsonProperty("description")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @JsonProperty("status")
        @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
        String status,

        @JsonProperty("created_by") String createdBy,

        @JsonProperty("updated_by") String updatedBy
) {
}
