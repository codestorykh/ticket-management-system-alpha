package com.codestorykh.user.service;

import com.codestorykh.user.dto.request.CreateRoleRequestDTO;
import com.codestorykh.user.dto.response.RoleResponseDTO;
import com.codestorykh.user.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

    RoleResponseDTO create(CreateRoleRequestDTO createRoleRequestDTO);

    RoleResponseDTO update(Long id, CreateRoleRequestDTO createRoleRequestDTO);

    RoleResponseDTO findById(Long id);

    RoleResponseDTO findByName(String name);

    List<Role> findByNameIn(Set<String> roleNames);

    List<RoleResponseDTO> findAll();

    List<RoleResponseDTO> findAllRoleActive(String status);

}
