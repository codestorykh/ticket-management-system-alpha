package com.codestorykh.user.service.impl;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.dto.EmptyObject;
import com.codestorykh.common.dto.PageableResponseVO;
import com.codestorykh.common.dto.UserRequest;
import com.codestorykh.common.dto.UserResponse;
import com.codestorykh.common.exception.BusinessException;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.constant.Constant;
import com.codestorykh.user.dto.request.UserChangePasswordRequest;
import com.codestorykh.user.dto.request.UserFilterRequest;
import com.codestorykh.user.dto.response.UserPaginationResponse;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.exception.UserValidationException;
import com.codestorykh.user.repository.GroupRepository;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.repository.UserRepository;
import com.codestorykh.user.service.UserSearchService;
import com.codestorykh.user.service.UserService;
import com.codestorykh.user.service.handler.PageableResponseHandlerService;
import com.codestorykh.user.service.handler.UserHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserHandlerService userHandlerService;
    private final UserSearchService userSearchService;
    private final PageableResponseHandlerService pageableResponseHandlerService;


    @Value("${jwt.default-password:'mypwd@123'}")
    private String defaultPassword;

    @Override
    @Transactional
    public ResponseErrorTemplate create(UserRequest userRequest) {
        ResponseErrorTemplate errorTemplate = userHandlerService.userRequestValidation(userRequest);
        if(errorTemplate != null && errorTemplate.isError()) {
            return errorTemplate;
        }

        // Here you would typically convert the DTO to an entity, save it to the database,

        if(!StringUtils.hasText(userRequest.username())) {
            throw new UserValidationException("username", "Username cannot be empty");
        }
        if(!StringUtils.hasText(userRequest.password())) {
            throw new UserValidationException("password", "Password cannot be empty");
        }
        if(userRepository.findByUsername(userRequest.username()).isPresent()) {
            throw new UserValidationException("username", "Username already exists");
        }

        User user = new User();
        user = userHandlerService.mapUserRequestToUser(userRequest, user);;
        user.setStatus(Constant.ACTIVE);

        // handle roles
        if(userRequest.roles() == null || userRequest.roles().isEmpty()) {
            roleRepository.findByName(Constant.USER).ifPresent(user::addRole);
        }else {
            roleRepository.findAllByNameIn(userRequest.roles()).forEach(user::addRole);
        }

        // handle groups
        if (userRequest.groups() != null && !userRequest.groups().isEmpty()) {
            groupRepository.findAllByNameIn(userRequest.groups()).forEach(user::addGroup);
        }
        userRepository.save(user);

        userRepository.save(user);
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                userHandlerService.mapUserToUserResponse(user),
                false);
    }

    @Override
    @Transactional
    public ResponseErrorTemplate update(Long id, UserRequest userRequest) {
        Optional<User> userOptional = userRepository.findFirstById(id);
        if (userOptional.isEmpty()) {
            log.error("Update user not found with id: {}", id);
            var msg = String.format(ApiConstant.USER_ID_NOT_FOUND.getDescription(), id);
            return new ResponseErrorTemplate(msg,
                    ApiConstant.USER_NOT_FOUND_CODE.getKey(), new Object(), true);
        }
        User user = userOptional.get();
        user = userHandlerService.mapUserRequestToUser(userRequest, user);
        user.setStatus(userRequest.status());
        userRepository.saveAndFlush(user);

        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                userHandlerService.mapUserToUserResponse(user),
                false);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        var msg = String.format(ApiConstant.USER_NAME_NOT_FOUND.getDescription(), id);
        return user.map(value -> new ResponseErrorTemplate(
                        ApiConstant.SUCCESS.getDescription(),
                        ApiConstant.SUCCESS.getKey(),
                        userHandlerService.mapUserToUserResponse(value),
                        false))
                .orElseGet(() ->
                        new ResponseErrorTemplate(
                                msg,
                                ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                                new EmptyObject(),
                                true));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findAll(UserFilterRequest userFilterRequest) {
        try {
            // Use UserSearchService to handle the search and pagination
            PageableResponseVO<User> pageResponse = userSearchService.searchUsers(userFilterRequest);

            if (pageResponse == null || pageResponse.getContent().isEmpty()) {
                log.info("No users found with filter: {}", userFilterRequest);
                return new ResponseErrorTemplate(
                        ApiConstant.USER_NOT_FOUND_CODE.getDescription(),
                        ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                        new Object(),
                        true
                );
            }

            // Convert users to response DTOs using stream API
            List<UserResponse> userResponses = pageResponse.getContent().stream()
                    .map(userHandlerService::mapUserToUserResponse)
                    .collect(Collectors.toList());

            // Create pagination response with metadata
            UserPaginationResponse userPaginationResponse = new UserPaginationResponse(
                    userResponses,
                    pageableResponseHandlerService.handlePaginationResponse(
                            pageResponse.getTotalElements(),
                            pageResponse.getPageNumber(),
                            pageResponse.getPageSize()
                    )
            ).withMetadata("total_users", pageResponse.getTotalElements())
                    .withMetadata("current_page", pageResponse.getPageNumber())
                    .withMetadata("page_size", pageResponse.getPageSize())
                    .withMetadata("has_next", pageResponse.hasNext())
                    .withMetadata("has_previous", pageResponse.hasPrevious());

            return new ResponseErrorTemplate(
                    ApiConstant.SUCCESS.getDescription(),
                    ApiConstant.SUCCESS.getKey(),
                    userPaginationResponse,
                    false
            );

        } catch (BusinessException e) {
            log.error("Business error retrieving users with filter {}: {}", userFilterRequest, e.getMessage());
            return new ResponseErrorTemplate(
                    e.getMessage(),
                    ApiConstant.BUSINESS_ERROR.getKey(),
                    new Object(),
                    true
            );
        } catch (Exception e) {
            log.error("Unexpected error retrieving users with filter {}: {}", userFilterRequest, e.getMessage());
            return new ResponseErrorTemplate(
                    ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey(),
                    new Object(),
                    true
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findByUsername(String username) {
        Optional<User> user = userRepository.findFirstByUsernameAndStatus(username, ApiConstant.ACTIVE.getKey());

        var msg = String.format(ApiConstant.USER_NAME_NOT_FOUND.getDescription(), username);
        return user.map(value -> new ResponseErrorTemplate(
                        ApiConstant.SUCCESS.getDescription(),
                        ApiConstant.SUCCESS.getKey(),
                        userHandlerService.mapUserToUserResponse(value),
                        false))
                .orElseGet(() -> new ResponseErrorTemplate(
                        msg,
                        ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                        new Object(),
                        true));
    }

    @Override
    @Transactional
    public ResponseErrorTemplate changePassword(Long id, UserChangePasswordRequest userChangePasswordRequest) {
        try {
            // Validate request
            if (userChangePasswordRequest == null) {
                log.error("Change password request is null for user id: {}", id);
                return new ResponseErrorTemplate(
                        ApiConstant.INVALID_REQUEST.getDescription(),
                        ApiConstant.INVALID_REQUEST.getKey(),
                        new Object(),
                        true
                );
            }

            // Find user
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("User not found with id: {}", id);
                        return new BusinessException(
                                String.format(ApiConstant.USER_ID_NOT_FOUND.getDescription(), id)
                        );
                    });

            // Validate current password
            if (!passwordEncoder.matches(userChangePasswordRequest.password(), user.getPassword())) {
                log.error("Incorrect current password for user id: {}", id);
                return new ResponseErrorTemplate(
                        ApiConstant.INCORRECT_PASSWORD.getDescription(),
                        ApiConstant.INCORRECT_PASSWORD.getKey(),
                        new Object(),
                        true
                );
            }

            // Validate new password
            if (userChangePasswordRequest.newPassword().equals(userChangePasswordRequest.password())) {
                log.error("New password must be different from current password for user id: {}", id);
                return new ResponseErrorTemplate(
                        ApiConstant.NEW_PASSWORD_SAME.getDescription(),
                        ApiConstant.NEW_PASSWORD_SAME.getKey(),
                        new Object(),
                        true
                );
            }

            // Update password
            user.setPassword(passwordEncoder.encode(userChangePasswordRequest.password()));
            userRepository.save(user);

            // Log success
            log.info("Password changed successfully for user id: {}", id);

            return new ResponseErrorTemplate(
                    ApiConstant.SUCCESS.getDescription(),
                    ApiConstant.SUCCESS.getKey(),
                    new Object(),
                    false
            );
        } catch (BusinessException e) {
            log.error("Business error changing password for user id {}: {}", id, e.getMessage());
            return new ResponseErrorTemplate(
                    e.getMessage(),
                    ApiConstant.BUSINESS_ERROR.getKey(),
                    new Object(),
                    true
            );
        } catch (Exception e) {
            log.error("Error changing password for user id {}: {}", id, e.getMessage());
            return new ResponseErrorTemplate(
                    ApiConstant.INTERNAL_SERVER_ERROR.getDescription(),
                    ApiConstant.INTERNAL_SERVER_ERROR.getKey(),
                    new Object(),
                    true
            );
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate disActivateUser(Set<Long> ids, String status) {
        List<User> users = userRepository.findAllByIdIn(ids);
        if (users.isEmpty()) {
            log.info("User not found with ids: {}", ids);

            return new ResponseErrorTemplate(
                    ApiConstant.USER_NOT_FOUND_CODE.getDescription(),
                    ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                    new Object(),
                    true);
        }

        users.forEach(user -> {
            user.setStatus(Optional.ofNullable(status).orElse(ApiConstant.IN_ACTIVE.getKey()));
            userRepository.saveAndFlush(user);
        });
        //refreshTokenService.deleteAllTokenByUserId(users);

        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                new Object(),
                false);
    }

    @Override
    @Transactional
    public ResponseErrorTemplate resetPassword(Set<Long> ids) {
        List<User> users = userRepository.findAllByIdIn(ids);
        if (users.isEmpty()) {
            log.info("reset user password not found with user ids: {}", ids);
            return new ResponseErrorTemplate(
                    ApiConstant.USER_NOT_FOUND_CODE.getDescription(),
                    ApiConstant.USER_NOT_FOUND_CODE.getKey(),
                    new Object(),
                    true);
        }

        users.forEach(user -> {
            user.setPassword(passwordEncoder.encode(defaultPassword));
            userRepository.saveAndFlush(user);
        });

        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                new Object(),
                false);
    }
}
