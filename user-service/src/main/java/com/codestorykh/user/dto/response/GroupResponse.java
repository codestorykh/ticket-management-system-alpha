package com.codestorykh.user.dto.response;

import java.time.LocalDateTime;
import java.util.Set;


public record GroupResponse(
        Long id,
        String name,
        String description,
        Set<String> roles,
        Set<String> permissions,
        String status,
        String createdBy,
        String updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int memberCount
) {
} 