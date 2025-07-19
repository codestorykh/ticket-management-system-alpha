package com.codestorykh.user.service.handler;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.criteria.BaseSearchCriteria;
import com.codestorykh.common.criteria.SearchCriteria;
import com.codestorykh.common.criteria.SearchOperation;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.RoleFilterRequest;
import com.codestorykh.user.dto.request.RoleRequest;
import com.codestorykh.user.dto.response.RoleResponse;
import com.codestorykh.user.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class RoleHandlerService {


    public Role convertRoleRequestToRole(RoleRequest roleRequest, Role role){
        role.setDescription(roleRequest.description());
        role.setName(roleRequest.name());
        role.setCreatedBy(roleRequest.createdBy());
        role.setStatus(roleRequest.status());
        return role;
    }

    public RoleResponse convertRoleToRoleResponse(Role role){
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getStatus(),
                role.getCreatedBy(),
                role.getCreatedAt() != null ? role.getCreatedAt().toString() : null,
                role.getUpdatedBy(),
                role.getUpdatedAt() != null ? role.getUpdatedAt().toString() : null
        );
    }

    public void searchRoleByName(RoleFilterRequest roleFilterRequest,
                                 BaseSearchCriteria baseSearchCriteria){
        if(StringUtils.hasText(roleFilterRequest.getName())){
            baseSearchCriteria.addCriteria(
                    new SearchCriteria("name", roleFilterRequest.getName(), SearchOperation.EQUAL));
        }
    }

    public ResponseErrorTemplate roleRequestValidation(RoleRequest roleRequest) {
        try {
            if (roleRequest == null) {
                log.error("Role request is null");
                return new ResponseErrorTemplate(
                    ApiConstant.INVALID_REQUEST.getDescription(),
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            // Validate name
            if (roleRequest.name() == null || roleRequest.name().trim().isEmpty()) {
                log.error("Role name is required");
                return new ResponseErrorTemplate(
                    "Role name is required",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            if (roleRequest.name().length() < 3 || roleRequest.name().length() > 50) {
                log.error("Role name must be between 3 and 50 characters");
                return new ResponseErrorTemplate(
                    "Role name must be between 3 and 50 characters",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            if (!roleRequest.name().matches("^[a-zA-Z0-9\\s_-]+$")) {
                log.error("Role name can only contain letters, numbers, spaces, underscores, and hyphens");
                return new ResponseErrorTemplate(
                    "Role name can only contain letters, numbers, spaces, underscores, and hyphens",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            // Validate code
            if (roleRequest.code() == null || roleRequest.code().trim().isEmpty()) {
                log.error("Role code is required");
                return new ResponseErrorTemplate(
                    "Role code is required",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            if (roleRequest.code().length() < 2 || roleRequest.code().length() > 20) {
                log.error("Role code must be between 2 and 20 characters");
                return new ResponseErrorTemplate(
                    "Role code must be between 2 and 20 characters",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            if (!roleRequest.code().matches("^[A-Z0-9_]+$")) {
                log.error("Role code can only contain uppercase letters, numbers, and underscores");
                return new ResponseErrorTemplate(
                    "Role code can only contain uppercase letters, numbers, and underscores",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            // Validate description
            if (roleRequest.description() != null && roleRequest.description().length() > 500) {
                log.error("Role description cannot exceed 500 characters");
                return new ResponseErrorTemplate(
                    "Role description cannot exceed 500 characters",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            // Validate status
            if (roleRequest.status() == null || roleRequest.status().trim().isEmpty()) {
                log.error("Role status is required");
                return new ResponseErrorTemplate(
                    "Role status is required",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            if (!ApiConstant.ACTIVE.getKey().equals(roleRequest.status()) && 
                !ApiConstant.IN_ACTIVE.getKey().equals(roleRequest.status())) {
                log.error("Invalid role status: {}", roleRequest.status());
                return new ResponseErrorTemplate(
                    "Invalid role status. Must be either ACTIVE or INACTIVE",
                    ApiConstant.INVALID_REQUEST.getKey(),
                    new Object(),
                    true
                );
            }

            return null; // Validation passed
        } catch (Exception e) {
            log.error("Error validating role request: {}", e.getMessage());
            return new ResponseErrorTemplate(
                "Error validating role request: " + e.getMessage(),
                ApiConstant.INTERNAL_SERVER_ERROR.getKey(),
                new Object(),
                true
            );
        }
    }

}
