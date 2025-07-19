package com.codestorykh.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("username") String username,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("user_img") String userImg,
        @JsonProperty("gender") String gender,
        @JsonProperty("date_of_birth") String dateOfBirth,
        @JsonProperty("password") String password,
        @JsonProperty("email") String email,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("created_on") LocalDateTime createdOn,
        @JsonProperty("last_login") LocalDateTime lastLogin,
        @JsonProperty("login_attempt") Integer loginAttempt,
        @JsonProperty("max_attempt") Integer maxAttempt,
        @JsonProperty("status") String status,
        @JsonProperty("roles") List<String> roles
        ) {

}