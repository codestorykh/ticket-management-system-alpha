package com.codestorykh.user.service;

import com.codestorykh.user.dto.request.CreateRoleRequestDTO;
import com.codestorykh.user.dto.response.CreateRoleResponseDTO;

import java.util.List;

public interface RoleService {

    CreateRoleResponseDTO create(CreateRoleRequestDTO createRoleRequestDTO);

    CreateRoleResponseDTO update(Long id, CreateRoleRequestDTO createRoleRequestDTO);

    CreateRoleResponseDTO findById(Long id);

    CreateRoleResponseDTO findByName(String name);

    List<CreateRoleResponseDTO> findAll();

    List<CreateRoleResponseDTO> findAllRoleActive(String status);

}
