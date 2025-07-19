package com.codestorykh.user.dto.request;

import com.codestorykh.common.dto.PageableRequestVO;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserFilterRequest extends PageableRequestVO {
    
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 50, message = "Username must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;

    @Size(max = 20, message = "Status must not exceed 20 characters")
    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
    private String status;

    @Size(max = 50, message = "Role must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Role can only contain uppercase letters, numbers, and underscores")
    private String role;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @Size(max = 50, message = "Sort field must not exceed 50 characters")
    @Pattern(regexp = "^(name|username|email|status|createdAt)$", message = "Invalid sort field")
    private String sortBy;
    
    private boolean desc;

    public UserFilterRequest() {
        super();
        // Set default values
        this.setPageNumber(0);
        this.setPageSize(10);
        this.desc = false;
    }

    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }

    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    public boolean hasNameFilter() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean hasUsernameFilter() {
        return username != null && !username.trim().isEmpty();
    }

    public boolean hasEmailFilter() {
        return email != null && !email.trim().isEmpty();
    }

    public boolean hasStatusFilter() {
        return status != null && !status.trim().isEmpty();
    }

    public boolean hasRoleFilter() {
        return role != null && !role.trim().isEmpty();
    }
}
