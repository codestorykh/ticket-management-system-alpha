package com.codestorykh.user.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;

public interface AuthService {

    ResponseErrorTemplate login(String username);
}
