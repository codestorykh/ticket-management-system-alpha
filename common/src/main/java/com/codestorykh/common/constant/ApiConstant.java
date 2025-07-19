package com.codestorykh.common.constant;

import lombok.Getter;

@Getter
public enum ApiConstant {
    
    // Success messages
    LOGOUT_SUCCESS("200", "Logout successfully."),
    LOGIN_SUCCESS("200", "Login successfully."),
    REFRESH_TOKEN_SUCCESS("200", "Refresh token successfully."),
    SUCCESS("200", "Successfully!"),
    FAILED("400", "Failed!"),
    CREATED("201", "Created successfully."),

    // Error messages
    GENERAL_ERROR("500", "Something went wrong on our end. Please try again later."),
    USER_ID_NOT_FOUND("404", "User ID %s not found."),
    USER_NAME_NOT_FOUND("404", "User name %s not found."),
    USER_NOT_FOUND_CODE("404", "User not found."),
    ROLE_NOT_FOUND("404", "Role not found."),
    UN_AUTHORIZATION("401", "Unauthorized access."),
    FORBIDDEN("403", "Forbidden access."),
    BAD_REQUEST("400", "Bad request."),
    INTERNAL_SERVER_ERROR("500", "Internal server error."),
    NOT_IMPLEMENTED("501", "Not implemented."),
    SERVICE_UNAVAILABLE("503", "Service unavailable."),
    GATEWAY_TIMEOUT("504", "Gateway timeout."),
    INVALID_REFRESH_TOKEN("400", "Invalid refresh token."),
    INVALID_ACCESS_TOKEN("400", "Invalid access token."),
    INVALID_USERNAME_PASSWORD("400", "Invalid username or password."),
    INVALID_USER_STATUS("400", "Invalid user status."),
    INVALID_USER_TYPE("400", "Invalid user type."),
    INCORRECT_PASSWORD("400", "Password incorrect."),
    NEW_PASSWORD_SAME("400", "New password cannot be the same as the old password."),
    INVALID_REQUEST("400", "Invalid request."),
    BUSINESS_ERROR("400", "Business error."),
    ROLE_NAME_NOT_FOUND("404", "Role name %s not found."),
    ROLE_ID_NOT_FOUND("404", "Role ID %s not found."),

    // Status codes
    ACTIVE("ACTIVE", "Active"),
    IN_ACTIVE("INACTIVE", "Inactive"),
    BLK("BLOCK", "Blocked");

    private final String key;
    private final String description;

    ApiConstant(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getFormattedDescription(Object... args) {
        return String.format(description, args);
    }
}
