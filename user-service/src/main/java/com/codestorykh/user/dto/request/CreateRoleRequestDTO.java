package com.codestorykh.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateRoleRequestDTO {

    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    private String status;
}
