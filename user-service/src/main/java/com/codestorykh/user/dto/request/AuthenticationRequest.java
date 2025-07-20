package com.codestorykh.user.dto.request;

public record AuthenticationRequest(
        String username,
        String password){}