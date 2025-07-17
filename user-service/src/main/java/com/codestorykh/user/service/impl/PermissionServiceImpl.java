package com.codestorykh.user.service.impl;

import com.codestorykh.user.constant.Constant;
import com.codestorykh.user.dto.request.CreatePermissionRequestDTO;
import com.codestorykh.user.dto.response.PermissionResponseDTO;
import com.codestorykh.user.entity.Permission;
import com.codestorykh.user.entity.Role;
import com.codestorykh.user.repository.PermissionRepository;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponseDTO create(CreatePermissionRequestDTO permissionRequestDTO) {
        if (permissionRepository.existsByName(permissionRequestDTO.getName())) {
            throw new IllegalArgumentException("Permission with name '" + permissionRequestDTO.getName() + "' already exists");
        }
        Permission permission = mapToPermission(permissionRequestDTO);
        permissionRepository.save(permission);

        return mapToPermissionResponse(permission);
    }

    @Override
    public PermissionResponseDTO update(Long id, CreatePermissionRequestDTO permissionDetails) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + id));

        // Check if the new name conflicts with existing permissions (excluding current permission)
        if (!permission.getName().equals(permissionDetails.getName()) &&
                permissionRepository.existsByName(permissionDetails.getName())) {
            throw new IllegalArgumentException("Permission with name '" + permissionDetails.getName() + "' already exists");
        }

        permission.setName(permissionDetails.getName());
        permission.setDescription(permissionDetails.getDescription());
        permission.setStatus(permissionDetails.getStatus());

        permissionRepository.save(permission);

        return mapToPermissionResponse(permission);
    }

    @Override
    public PermissionResponseDTO assignRoleToPermission(Long permissionId, Long roleId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        permission.addRole(role);
        permissionRepository.save(permission);

        return mapToPermissionResponse(permission);
    }

    @Override
    public PermissionResponseDTO removeRoleFromPermission(Long permissionId, Long roleId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        permission.removeRole(role);
        permissionRepository.save(permission);

        return mapToPermissionResponse(permission);
    }

    @Override
    public List<Permission> getPermissionsByNameIn(Set<String> names) {
        return permissionRepository.findAllByStatusAndNameIn(Constant.ACTIVE, names);
    }

    private Permission mapToPermission(CreatePermissionRequestDTO request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permission.setStatus(Constant.ACTIVE);
        return permission;
    }

    private PermissionResponseDTO mapToPermissionResponse(Permission permission) {
        PermissionResponseDTO response = new PermissionResponseDTO();
        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        response.setStatus(permission.getStatus());
        return response;
    }
}