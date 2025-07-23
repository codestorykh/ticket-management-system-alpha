package com.codestorykh.user.controller;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.dto.UserRequest;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.AuthenticationRequest;
import com.codestorykh.user.dto.request.RefreshTokenRequest;
import com.codestorykh.user.service.AuthService;
import com.codestorykh.user.service.UserService;
import com.codestorykh.user.service.impl.CustomUserDetailService;
import com.codestorykh.user.service.impl.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/api/public/users")
@RequiredArgsConstructor
public class PublicController {

    private final AuthService authService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailService customUserDetailService;

    @PostMapping("/registration")
    public ResponseEntity<ResponseErrorTemplate> register(@RequestBody UserRequest userRequest) {
        log.info("Intercept registration new user with req: {}", userRequest);
        return ResponseEntity.ok(userService.create(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseErrorTemplate> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authService.login(authenticationRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseErrorTemplate> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Intercept logout refresh token with req: {}", refreshTokenRequest);
        refreshTokenService.deleteToken(refreshTokenRequest.refreshToken());
        var responseErrorTemplate = new ResponseErrorTemplate(
                ApiConstant.LOGOUT_SUCCESS.getDescription(),
                ApiConstant.LOGOUT_SUCCESS.getKey(),
                new EmptyObject(),
                false);
        return ResponseEntity.ok(responseErrorTemplate);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseErrorTemplate> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(refreshTokenRequest));
    }

}