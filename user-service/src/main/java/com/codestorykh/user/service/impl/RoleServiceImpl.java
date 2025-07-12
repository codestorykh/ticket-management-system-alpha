package com.codestorykh.user.service.impl;

import com.codestorykh.user.constant.Constant;
import com.codestorykh.user.dto.request.CreateRoleRequestDTO;
import com.codestorykh.user.dto.response.CreateRoleResponseDTO;
import com.codestorykh.user.entity.Role;
import com.codestorykh.user.exception.RoleValidationException;
import com.codestorykh.user.mapper.RoleMapper;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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
    public CreateRoleResponseDTO create(CreateRoleRequestDTO createRoleRequestDTO) {
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
    public CreateRoleResponseDTO update(Long id, CreateRoleRequestDTO createRoleRequestDTO) {
        return null;
    }

    @Override
    public CreateRoleResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public CreateRoleResponseDTO findByName(String name) {
        return roleRepository.findByName(name)
                .map(roleMapper::toCreateRoleResponseDTO).orElse(null);
    }

    @Override
    public List<CreateRoleResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public List<CreateRoleResponseDTO> findAllRoleActive(String status) {
        return List.of();
    }
}
