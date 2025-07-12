package com.codestorykh.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserRequestDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("roles")
    private Set<String> roles;

    @JsonProperty("groups")
    private Set<String> groups;

}
