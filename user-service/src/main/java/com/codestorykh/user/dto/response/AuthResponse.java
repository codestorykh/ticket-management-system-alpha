package com.codestorykh.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse {

    private String token;

    @JsonProperty("token_type")
    private String tokenTye;
    private String username;
    private String role;
}
