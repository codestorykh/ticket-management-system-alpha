package com.codestorykh.user.controller;

import com.codestorykh.user.dto.request.CreateRoleRequestDTO;
import com.codestorykh.user.dto.response.RoleResponseDTO;
import com.codestorykh.user.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody CreateRoleRequestDTO request) {
        return ResponseEntity.ok(roleService.create(request));
    }
}
