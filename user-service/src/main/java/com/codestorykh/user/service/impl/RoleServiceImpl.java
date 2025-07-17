package com.codestorykh.user.service.impl;

import com.codestorykh.user.constant.Constant;
import com.codestorykh.user.dto.request.CreateRoleRequestDTO;
import com.codestorykh.user.dto.response.RoleResponseDTO;
import com.codestorykh.user.entity.Role;
import com.codestorykh.user.exception.RoleValidationException;
import com.codestorykh.user.mapper.RoleMapper;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleMapper roleMapper,
                           RoleRepository roleRepository) {
        this.roleMapper = roleMapper;
        this.roleRepository = roleRepository;
    }


    @Override
    public RoleResponseDTO create(CreateRoleRequestDTO createRoleRequestDTO) {
        if(!StringUtils.hasText(createRoleRequestDTO.getName())) {
            throw new RoleValidationException("name", "Role name cannot be empty");
        }
        if(roleRepository.findByName(createRoleRequestDTO.getName()).isPresent()) {
            throw new RoleValidationException("name", "Role name already exists");
        }

        Role role = roleMapper.toRole(createRoleRequestDTO);
        role.setStatus(Constant.ACTIVE);

        roleRepository.save(role);

        return roleMapper.toCreateRoleResponseDTO(role);
    }

    @Override
    public RoleResponseDTO update(Long id, CreateRoleRequestDTO roleRequestDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));

        final String roleName = roleRequestDTO.getName();

        // Check if the new name conflicts with existing roles (excluding current role)
        if (!role.getName().equals(roleName) &&
                roleRepository.existsByName(roleName)) {
            throw new IllegalArgumentException("Role with name '" + roleName + "' already exists");
        }

        role.setName(roleName);
        role.setDescription(roleRequestDTO.getDescription());
        role.setStatus(Constant.ACTIVE);

        roleRepository.save(role);

        return roleMapper.toCreateRoleResponseDTO(role);
    }

    @Override
    public RoleResponseDTO findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));

        return roleMapper.toCreateRoleResponseDTO(role);
    }

    @Override
    public RoleResponseDTO findByName(String name) {
        return roleRepository.findByName(name)
                .map(roleMapper::toCreateRoleResponseDTO).orElse(null);
    }

    @Override
    public List<Role> findByNameIn(Set<String> roleNames) {
        return roleRepository.findAllByNameIn(roleNames);
    }

    @Override
    public List<RoleResponseDTO> findAll() {
        return convertToResponseList(roleRepository.findAll());
    }

    @Override
    public List<RoleResponseDTO> findAllRoleActive(String status) {
        return convertToResponseList(roleRepository.findAllByStatus(status));
    }

    private List<RoleResponseDTO> convertToResponseList(List<Role> roles) {
        if (roles.isEmpty()) {

            return List.of();
        }
        return roles.stream()
                .map(roleMapper::toCreateRoleResponseDTO)
                .toList();
    }

}
