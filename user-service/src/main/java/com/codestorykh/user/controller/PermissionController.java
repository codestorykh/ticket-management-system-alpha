package com.codestorykh.user.controller;

import com.codestorykh.user.dto.request.CreatePermissionRequestDTO;
import com.codestorykh.user.dto.response.PermissionResponseDTO;
import com.codestorykh.user.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @PostMapping
    public ResponseEntity<PermissionResponseDTO> createPermission(@RequestBody CreatePermissionRequestDTO permission) {
        try {
            return ResponseEntity.ok(permissionService.create(permission));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDTO> updatePermission(@PathVariable Long id,
                                                                  @RequestBody CreatePermissionRequestDTO permissionDetails) {
        try {
            return ResponseEntity.ok(permissionService.update(id, permissionDetails));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{permissionId}/roles/{roleId}")
    public ResponseEntity<PermissionResponseDTO> assignRoleToPermission(@PathVariable Long permissionId,
                                                                        @PathVariable Long roleId) {
        try {
            return ResponseEntity.ok(permissionService.assignRoleToPermission(permissionId, roleId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{permissionId}/roles/{roleId}")
    public ResponseEntity<PermissionResponseDTO> removeRoleFromPermission(@PathVariable Long permissionId,
                                                                          @PathVariable Long roleId) {
        try {
            return ResponseEntity.ok(permissionService.removeRoleFromPermission(permissionId, roleId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

} 