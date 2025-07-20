package com.codestorykh.user.service.impl;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.exception.CustomMessageException;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.common.exception.SystemException;
import com.codestorykh.user.dto.request.RefreshTokenRequest;
import com.codestorykh.user.dto.response.AuthenticationResponse;
import com.codestorykh.user.entity.CustomUserDetail;
import com.codestorykh.user.entity.RefreshToken;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.repository.RefreshTokenRepository;
import com.codestorykh.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailService customUserDetailService;


    public void createRefreshToken(String username, String token, Date tokenExpiration) {
        Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, ApiConstant.ACTIVE.getKey());
        if (user.isEmpty()) {
            log.error("User not found with username: {}", username);
            throw new CustomMessageException(
                    ApiConstant.USER_NAME_NOT_FOUND.getDescription(),
                    ApiConstant.USER_NAME_NOT_FOUND.getKey(),
                    new EmptyObject(),
                    HttpStatus.NOT_FOUND);
        }

        RefreshToken refreshToken;
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user.get());
        if(refreshTokenOptional.isPresent()) {
            log.info("Refresh refreshToken already exists..!");
            refreshToken = refreshTokenOptional.get();
            refreshToken.setToken(token);
            refreshToken.setExpiryDate(tokenExpiration);

        }else {
            refreshToken = RefreshToken.builder()
                    .user(user.get())
                    .token(token)
                    .expiryDate(tokenExpiration)
                    .build();
        }
        log.info("Refresh refreshToken created successfully..!");
        refreshTokenRepository.save(refreshToken);
    }

    public ResponseErrorTemplate refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            ResponseErrorTemplate errorTemplate = this.findByToken(refreshTokenRequest.refreshToken())
                    .map(this::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(userInfo -> {
                        final CustomUserDetail customUserDetail = customUserDetailService.customUserDetail(userInfo.getUsername());
                        final String accessToken = "SSS";//jwtService.generateToken(customUserDetail);
                        var responseToken = new AuthenticationResponse(accessToken, refreshTokenRequest.refreshToken());
                        return new ResponseErrorTemplate(
                                ApiConstant.REFRESH_TOKEN_SUCCESS.getDescription(),
                                ApiConstant.REFRESH_TOKEN_SUCCESS.getKey(),
                                responseToken,
                                false);
                    }).orElseThrow();
            return errorTemplate;
        }catch (Exception e) {
            log.error("Error while refreshing token with request: {}", refreshTokenRequest, e);
            throw new CustomMessageException(
                    ApiConstant.UN_AUTHORIZATION.getDescription(),
                    ApiConstant.UN_AUTHORIZATION.getKey(),
                    new EmptyObject(),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Date.from(Instant.now())) < 0) {
            refreshTokenRepository.delete(token);
            log.info("Refresh refreshToken is expired. Please make a new login..!");
            throw new SystemException(token.getToken() + " Refresh refreshToken is expired. Please make a new login..!");
        }
        return token;
    }

    public void deleteToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public void deleteAllTokenByUserId(List<User> users) {
        refreshTokenRepository.deleteAllByUserIn(users);
    }

}