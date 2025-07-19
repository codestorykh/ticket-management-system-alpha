package com.codestorykh.user.service;

import com.codestorykh.user.dto.request.CreatePermissionRequestDTO;
import com.codestorykh.user.dto.response.PermissionResponseDTO;
import com.codestorykh.user.entity.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    PermissionResponseDTO create(CreatePermissionRequestDTO permissionRequestDTO);

    PermissionResponseDTO update(Long id, CreatePermissionRequestDTO permissionRequestDTO);

    PermissionResponseDTO assignRoleToPermission(Long permissionId, Long roleId);

    PermissionResponseDTO removeRoleFromPermission(Long permissionId, Long roleId);

    List<Permission> getPermissionsByNameIn(Set<String> names);
}
