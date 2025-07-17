package com.codestorykh.user.service.impl;

import com.codestorykh.user.constant.Constant;
import com.codestorykh.user.dto.request.CreateUserRequestDTO;
import com.codestorykh.user.dto.response.AuthResponse;
import com.codestorykh.user.dto.response.UserResponseDTO;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.exception.UserValidationException;
import com.codestorykh.user.mapper.UserMapper;
import com.codestorykh.user.repository.GroupRepository;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.repository.UserRepository;
import com.codestorykh.user.service.RoleService;
import com.codestorykh.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;


    @Override
    public AuthResponse create(CreateUserRequestDTO userRequestDTO) {
        // Here you would typically convert the DTO to an entity, save it to the database,

        if(!StringUtils.hasText(userRequestDTO.getUsername())) {
            throw new UserValidationException("username", "Username cannot be empty");
        }
        if(!StringUtils.hasText(userRequestDTO.getPassword())) {
            throw new UserValidationException("password", "Password cannot be empty");
        }
        if(userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            throw new UserValidationException("username", "Username already exists");
        }

        User user = userMapper.toUser(userRequestDTO);
        user.setStatus(Constant.ACTIVE);

        // handle roles
        if(userRequestDTO.getRoles() == null || userRequestDTO.getRoles().isEmpty()) {
            roleRepository.findByName(Constant.USER).ifPresent(user::addRole);
        }else {
            roleRepository.findAllByNameIn(userRequestDTO.getRoles()).forEach(user::addRole);
        }

        // handle groups
        if (userRequestDTO.getGroups() != null && !userRequestDTO.getGroups().isEmpty()) {
            groupRepository.findAllByNameIn(userRequestDTO.getGroups()).forEach(user::addGroup);
        }
        userRepository.save(user);

        return new AuthResponse();
    }

    @Override
    public UserResponseDTO update(Long id, CreateUserRequestDTO createUserRequestDTO) {
        return null;
    }
}
