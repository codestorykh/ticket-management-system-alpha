package com.codestorykh.user.service.impl;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.response.AuthenticationResponse;
import com.codestorykh.user.entity.CustomUserDetail;
import com.codestorykh.user.repository.UserRepository;
import com.codestorykh.user.service.AuthService;
import com.codestorykh.user.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public ResponseErrorTemplate login(String username){
        CustomUserDetail customUserDetail = customUserDetailService.customUserDetail(username);
        return new ResponseErrorTemplate(
                ApiConstant.LOGIN_SUCCESS.getDescription(),
                ApiConstant.LOGIN_SUCCESS.getKey(),
                new AuthenticationResponse(
                        jwtService.generateToken(customUserDetail),
                        jwtService.refreshToken(customUserDetail)),
                false);
    }

}
