package com.codestorykh.common.dto;

import java.time.LocalDateTime;

public record UserDto(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

} 