package com.codestorykh.user.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.RoleFilterRequest;
import com.codestorykh.user.dto.request.RoleRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service interface for managing roles in the system.
 * Provides methods for CRUD operations and role management.
 */
public interface RoleService {

    /**
     * Creates a new role in the system.
     *
     * @param request The role request containing role details
     * @return Response template containing the created role or error message
     */
    @Transactional
    ResponseErrorTemplate create(RoleRequest request);

    /**
     * Updates an existing role in the system.
     *
     * @param id The ID of the role to update
     * @param request The role request containing updated details
     * @return Response template containing the updated role or error message
     */
    @Transactional
    ResponseErrorTemplate update(Long id, RoleRequest request);

    /**
     * Retrieves a role by its ID.
     *
     * @param id The ID of the role to retrieve
     * @return Response template containing the role or error message
     */
    @Transactional(readOnly = true)
    ResponseErrorTemplate findById(Long id);

    /**
     * Retrieves roles based on filter criteria.
     *
     * @param filterRequest The filter criteria for role search
     * @return Response template containing the filtered roles or error message
     */
    @Transactional(readOnly = true)
    ResponseErrorTemplate findAll(RoleFilterRequest filterRequest);

    /**
     * Deletes a role from the system.
     *
     * @param id The ID of the role to delete
     * @return Response template containing success or error message
     */
    @Transactional
    ResponseErrorTemplate delete(Long id);

    /**
     * Deletes multiple roles from the system.
     *
     * @param ids The set of role IDs to delete
     * @return Response template containing success or error message
     */
    @Transactional
    ResponseErrorTemplate deleteAll(Set<Long> ids);

    /**
     * Retrieves a role by its name.
     *
     * @param name The name of the role to retrieve
     * @return Response template containing the role or error message
     */
    @Transactional(readOnly = true)
    ResponseErrorTemplate findByName(String name);

    /**
     * Activates or deactivates multiple roles.
     *
     * @param ids The set of role IDs to update
     * @param status The new status to set (ACTIVE/INACTIVE)
     * @return Response template containing success or error message
     */
    @Transactional
    ResponseErrorTemplate disActivateRole(Set<Long> ids, String status);
}
