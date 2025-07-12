package com.codestorykh.user.controller;

import com.codestorykh.user.dto.request.CreateUserRequestDTO;
import com.codestorykh.user.dto.response.AuthResponse;
import com.codestorykh.user.dto.response.CreateUserResponseDTO;
import com.codestorykh.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    private ResponseEntity<AuthResponse> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        return ResponseEntity.ok(userService.create(request));
    }
}
