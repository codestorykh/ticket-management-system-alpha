package com.codestorykh.user.controller;

import com.codestorykh.common.exception.GeneralErrorResponse;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.RoleFilterRequest;
import com.codestorykh.user.dto.request.RoleRequest;
import com.codestorykh.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

//@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("api/roles")
@Slf4j
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/filter")
    public ResponseEntity<ResponseErrorTemplate> filterRoles(@Validated @RequestBody RoleFilterRequest roleFilterRequest) {
        try {
            log.info("Filtering roles with request: {}", roleFilterRequest);
            ResponseErrorTemplate response = roleService.findAll(roleFilterRequest);
            return new ResponseEntity<>(response, response.isError() ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error filtering roles: {}", e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalErrors(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> getById(@PathVariable Long id) {
        log.info("Getting role by id: {}", id);
        try {
            ResponseErrorTemplate response = roleService.findById(id);
            return new ResponseEntity<>(response, response.isError() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting role by id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseErrorTemplate> create(@Validated @RequestBody RoleRequest roleRequest) {
        log.info("Creating new role: {}", roleRequest);
        try {
            ResponseErrorTemplate response = roleService.create(roleRequest);
            return new ResponseEntity<>(response, response.isError() ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating role: {}", e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> update(@PathVariable Long id,
                                                        @Validated @RequestBody RoleRequest roleRequest) {
        log.info("Updating role {} with data: {}", id, roleRequest);
        try {
            ResponseErrorTemplate response = roleService.update(id, roleRequest);
            return new ResponseEntity<>(response, response.isError() ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating role {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> delete(@PathVariable Long id) {
        log.info("Deleting role: {}", id);
        try {
            ResponseErrorTemplate response = roleService.delete(id);
            return new ResponseEntity<>(response, response.isError() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting role {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete/all")
    public ResponseEntity<ResponseErrorTemplate> deleteAll(@Validated @RequestBody Set<Long> ids) {
        log.info("Deleting multiple roles: {}", ids);
        try {
            ResponseErrorTemplate response = roleService.deleteAll(ids);
            return new ResponseEntity<>(response, response.isError() ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting roles {}: {}", ids, e.getMessage(), e);
            return new ResponseEntity<>(GeneralErrorResponse.generalErrors(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}