package com.codestorykh.user.dto.response;

import com.codestorykh.user.entity.Group;
import com.codestorykh.user.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class PermissionResponseDTO {

    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    private String status;

    @JsonProperty("roles")
    private Set<Role> roles;

    @JsonProperty("groups")
    private Set<Group> groups;
}
