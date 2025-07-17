package com.codestorykh.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CreateGroupRequestDTO {
    
    @NotBlank(message = "Group name is required")
    @Size(min = 2, max = 50, message = "Group name must be between 2 and 50 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private Set<String> roles;
    
    private Set<String> permissions;

    private String status;
} 