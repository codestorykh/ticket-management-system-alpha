package com.codestorykh.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GroupMemberRequest {
    
    @NotNull(message = "User IDs are required")
    private List<Long> userIds;
} 