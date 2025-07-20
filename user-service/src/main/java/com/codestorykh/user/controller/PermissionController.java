package com.codestorykh.user.controller;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.CreatePermissionRequest;
import com.codestorykh.user.dto.response.PermissionResponse;
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
    public ResponseEntity<ResponseErrorTemplate> createPermission(@RequestBody CreatePermissionRequest permission) {
        return ResponseEntity.ok(permissionService.create(permission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> updatePermission(@PathVariable Long id,
                                                               @RequestBody CreatePermissionRequest permissionDetails) {
        return ResponseEntity.ok(permissionService.update(id, permissionDetails));
    }


    @PostMapping("/{permissionId}/roles/{roleId}")
    public ResponseEntity<ResponseErrorTemplate> assignRoleToPermission(@PathVariable Long permissionId,
                                                                     @PathVariable Long roleId) {
        return ResponseEntity.ok(permissionService.assignRoleToPermission(permissionId, roleId));
    }

    @DeleteMapping("/{permissionId}/roles/{roleId}")
    public ResponseEntity<ResponseErrorTemplate> removeRoleFromPermission(@PathVariable Long permissionId,
                                                                       @PathVariable Long roleId) {
        return ResponseEntity.ok(permissionService.removeRoleFromPermission(permissionId, roleId));
    }

} 