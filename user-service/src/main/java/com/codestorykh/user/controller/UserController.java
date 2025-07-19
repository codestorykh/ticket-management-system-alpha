package com.codestorykh.user.controller;

import com.codestorykh.common.dto.UserRequest;
import com.codestorykh.common.exception.GeneralErrorResponse;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.UserChangePasswordRequest;
import com.codestorykh.user.dto.request.UserFilterRequest;
import com.codestorykh.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;


//@SecurityRequirement(name = "Bearer Authentication")
@Slf4j
@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<Object> findUserByUsername(Principal principal) {
        var username = principal.getName();
        log.info("User: username {} request get user data", username);
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PostMapping("filter")
    public ResponseEntity<ResponseErrorTemplate> filterUser(@Validated @RequestBody UserFilterRequest userFilterRequest) {
        try {
            log.info("Intercept filter user with: {}", userFilterRequest);

            // Validate pagination parameters
            if (userFilterRequest.getPageNumber() < 0) {
                return new ResponseEntity<>(
                        new ResponseErrorTemplate(
                                "Page number must be greater than or equal to 0",
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                new Object(),
                                true
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (userFilterRequest.getPageSize() < 1) {
                return new ResponseEntity<>(
                        new ResponseErrorTemplate(
                                "Page size must be greater than 0",
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                new Object(),
                                true
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            // Validate date range if provided
            if (userFilterRequest.hasDateRange() &&
                    userFilterRequest.getStartDate().isAfter(userFilterRequest.getEndDate())) {
                return new ResponseEntity<>(
                        new ResponseErrorTemplate(
                                "Start date cannot be after end date",
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                new Object(),
                                true
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            return new ResponseEntity<>(userService.findAll(userFilterRequest), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request parameters: {}", e.getMessage());
            return new ResponseEntity<>(
                    new ResponseErrorTemplate(
                            e.getMessage(),
                            String.valueOf(HttpStatus.BAD_REQUEST.value()),
                            new Object(),
                            true
                    ),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error("Error filtering users: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                    GeneralErrorResponse.generalErrors(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> getById(@PathVariable Long id) {
        log.info("Intercept check user id: {}", id);
        try {
            return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseErrorTemplate> create(@Validated @RequestBody UserRequest userRequest) {
        log.info("Intercept create new user with: {}", userRequest);
        try {
            return new ResponseEntity<>(userService.create(userRequest), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> update(@PathVariable Long id, @Validated @RequestBody UserRequest userRequest) {
        log.info("Intercept update user with: {}", userRequest);
        try {
            return new ResponseEntity<>(userService.update(id, userRequest), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/status/{status}")
    public ResponseEntity<ResponseErrorTemplate> disActiveUser(@PathVariable Set<Long> id, @PathVariable String status) {
        log.info("Intercept disable user with: {}", id);
        try {
            return new ResponseEntity<>(userService.disActivateUser(id, status), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalErrors(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/password/change")
    public ResponseEntity<ResponseErrorTemplate> changeUserPassword(@PathVariable Long id,
                                                                    @Validated @RequestBody UserChangePasswordRequest changePasswordRequest) {
        log.info("Intercept change password with: {}", changePasswordRequest);
        try {
            return new ResponseEntity<>(userService.changePassword(id, changePasswordRequest), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/password/reset")
    public ResponseEntity<ResponseErrorTemplate> resetUserPassword(@PathVariable Set<Long> id) {
        log.info("Intercept reset password with user id: {}", id);
        try {
            return new ResponseEntity<>(userService.resetPassword(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
