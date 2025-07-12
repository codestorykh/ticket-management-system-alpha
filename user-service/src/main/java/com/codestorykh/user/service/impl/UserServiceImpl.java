package com.codestorykh.user.service.impl;

import com.codestorykh.user.constant.Constant;
import com.codestorykh.user.dto.request.CreateUserRequestDTO;
import com.codestorykh.user.dto.response.AuthResponse;
import com.codestorykh.user.dto.response.CreateRoleResponseDTO;
import com.codestorykh.user.dto.response.CreateUserResponseDTO;
import com.codestorykh.user.entity.Role;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.exception.UserValidationException;
import com.codestorykh.user.mapper.UserMapper;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.repository.UserRepository;
import com.codestorykh.user.service.RoleService;
import com.codestorykh.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    @Override
    public AuthResponse create(CreateUserRequestDTO createUserRequestDTO) {
        // Here you would typically convert the DTO to an entity, save it to the database,

        if(!StringUtils.hasText(createUserRequestDTO.getUsername())) {
            throw new UserValidationException("username", "Username cannot be empty");
        }
        if(!StringUtils.hasText(createUserRequestDTO.getPassword())) {
            throw new UserValidationException("password", "Password cannot be empty");
        }
        if(userRepository.findByUsername(createUserRequestDTO.getUsername()).isPresent()) {
            throw new UserValidationException("username", "Username already exists");
        }

        User user = userMapper.toUser(createUserRequestDTO);
        user.setStatus(Constant.ACTIVE);

        // handle roles
        if(createUserRequestDTO.getRoles() == null || createUserRequestDTO.getRoles().isEmpty()) {
            roleRepository.findByName(Constant.USER).ifPresent(user::addRole);
        }else {
            roleRepository.findAllByNameIn(createUserRequestDTO.getRoles()).forEach(user::addRole);
        }

        // handle groups
        if(createUserRequestDTO.getGroups() != null && !createUserRequestDTO.getGroups().isEmpty()) {
            createUserRequestDTO.getGroups().forEach(group -> {
                // Assuming you have a method to find or create a group
                // Group foundGroup = groupService.findOrCreateGroup(group);
                // user.addGroup(foundGroup);
            });
        }

        userRepository.save(user);

        return new AuthResponse();
    }

    @Override
    public CreateUserResponseDTO update(Long id, CreateUserRequestDTO createUserRequestDTO) {
        return null;
    }
}
