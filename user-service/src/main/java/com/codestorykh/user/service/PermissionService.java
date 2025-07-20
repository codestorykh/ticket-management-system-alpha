package com.codestorykh.user.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.CreatePermissionRequest;
import com.codestorykh.user.entity.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    ResponseErrorTemplate create(CreatePermissionRequest permissionRequest);

    ResponseErrorTemplate update(Long id, CreatePermissionRequest permissionRequest);

    ResponseErrorTemplate assignRoleToPermission(Long permissionId, Long roleId);

    ResponseErrorTemplate removeRoleFromPermission(Long permissionId, Long roleId);

    List<Permission> getPermissionsByNameIn(Set<String> names);
}
