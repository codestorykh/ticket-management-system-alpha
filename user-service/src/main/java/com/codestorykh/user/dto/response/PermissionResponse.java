package com.codestorykh.user.dto.response;

import com.codestorykh.user.entity.Group;
import com.codestorykh.user.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;


public record PermissionResponse(Long id,
                                 String name,
                                 String description,
                                 String status,
                                 @JsonProperty("roles") Set<Role> roles,
                                 @JsonProperty("groups")
                                 Set<Group> groups) {
}
