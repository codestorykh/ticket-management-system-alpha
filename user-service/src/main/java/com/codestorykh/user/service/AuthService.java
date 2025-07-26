package com.codestorykh.user.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.AuthenticationRequest;

public interface AuthService {

    ResponseErrorTemplate login(AuthenticationRequest authenticationRequest);
}
