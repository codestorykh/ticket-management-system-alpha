package com.codestorykh.user.service.handler;

import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.dto.UserRequest;
import com.codestorykh.common.dto.UserResponse;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.entity.Role;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserHandlerService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserHandlerService(PasswordEncoder passwordEncoder,
                              UserRepository userRepository,
                              RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public ResponseErrorTemplate userRequestValidation(UserRequest userRequest) {

        if(ObjectUtils.isEmpty(userRequest.password())) {
          return new ResponseErrorTemplate(
                  "Password can't be blank or null.",
                    String.valueOf(HttpStatus.BAD_REQUEST),
                    new EmptyObject(),
                  true);
        }

        Optional<User> user = userRepository.findByUsernameOrEmail(userRequest.username(), userRequest.email());
        if(user.isPresent()){
            return new ResponseErrorTemplate(
                    "Username or Email already exists.",
                    String.valueOf(HttpStatus.BAD_REQUEST),
                    new EmptyObject(),
                    true);
        }

        List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
        for(var role : userRequest.roles()){
            if(!roles.contains(role)) {
                return new ResponseErrorTemplate(
                        "Role is invalid request.",
                        String.valueOf(HttpStatus.BAD_REQUEST),
                        new EmptyObject(),
                        true);
            }
        }
        return new ResponseErrorTemplate(
                "Success",
                String.valueOf(HttpStatus.OK),
                new EmptyObject(),
                false);
    }

    public User mapUserRequestToUser(final UserRequest userRequest,
                                     User user) {
        user.setUsername(userRequest.username());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());
        user.setUserType(userRequest.userImg());
        user.setUserType(userRequest.userType());
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setEmail(userRequest.email());
        user.setGender(userRequest.gender());
        user.setDateOfBirth(userRequest.dateOfBirth());
        user.setLoginAttempt(Optional.ofNullable(userRequest.loginAttempt()).orElse(0));

        return user;
    }

    public UserResponse mapUserToUserResponse(final User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserImage(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getCreatedAt(),
                user.getLastLogin(),
                user.getLoginAttempt(),
                user.getMaxAttempt(),
                user.getStatus(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }
}
