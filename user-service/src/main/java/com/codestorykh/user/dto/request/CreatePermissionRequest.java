package com.codestorykh.user.dto.request;

import com.codestorykh.user.entity.Group;
import com.codestorykh.user.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;


public record CreatePermissionRequest(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        @JsonProperty("roles") Set<Role> roles,
        @JsonProperty("groups") Set<Group> groups
) {
}
