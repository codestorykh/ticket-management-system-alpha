package com.codestorykh.user.dto.request;

import com.codestorykh.user.entity.Group;
import com.codestorykh.user.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CreatePermissionRequestDTO {

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
