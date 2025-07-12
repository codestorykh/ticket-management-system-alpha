package com.codestorykh.user.service;

import com.codestorykh.user.dto.request.CreateUserRequestDTO;
import com.codestorykh.user.dto.response.AuthResponse;
import com.codestorykh.user.dto.response.CreateUserResponseDTO;

public interface UserService {

    AuthResponse create(CreateUserRequestDTO createUserRequestDTO);

    CreateUserResponseDTO update(Long id, CreateUserRequestDTO createUserRequestDTO);

}
