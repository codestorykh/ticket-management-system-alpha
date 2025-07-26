package com.codestorykh.user.service;

import com.codestorykh.user.entity.CustomUserDetail;
import io.jsonwebtoken.Claims;

import java.security.Key;

public interface JwtService {

    Claims extractClaims(String token);
    Key getKey();
    String generateToken(com.codestorykh.user.entity.CustomUserDetail customUserDetail);
    String refreshToken(CustomUserDetail customUserDetail);
    boolean isValidToken(String token);
}