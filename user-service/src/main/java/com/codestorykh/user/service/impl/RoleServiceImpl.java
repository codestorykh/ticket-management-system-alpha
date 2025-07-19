package com.codestorykh.user.service.impl;

import com.codestorykh.common.constant.ApiConstant;
import com.codestorykh.common.criteria.BaseSearchCriteria;
import com.codestorykh.common.criteria.SearchCriteria;
import com.codestorykh.common.criteria.SearchOperation;
import com.codestorykh.common.dto.Metadata;
import com.codestorykh.common.dto.PageableResponseVO;
import com.codestorykh.common.exception.BusinessException;
import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.common.repository.BaseRepository;
import com.codestorykh.user.dto.request.RoleFilterRequest;
import com.codestorykh.user.dto.request.RoleRequest;
import com.codestorykh.user.dto.response.RolePaginationResponse;
import com.codestorykh.user.dto.response.RoleResponse;
import com.codestorykh.user.entity.Role;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.service.RoleService;
import com.codestorykh.user.service.handler.PageableResponseHandlerService;
import com.codestorykh.user.service.handler.RoleHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleHandlerService roleHandlerService;
    private final BaseRepository baseEntityService;
    private final PageableResponseHandlerService pageableResponseHandlerService;

    @Override
    @Transactional
    public ResponseErrorTemplate create(RoleRequest roleRequest) {
        try {
            // Validate request
            ResponseErrorTemplate validationError = roleHandlerService.roleRequestValidation(roleRequest);
            if (validationError != null && validationError.isError()) {
                return validationError;
            }

            // Check for duplicate role name
            if (roleRepository.findFirstByName(roleRequest.name()).isPresent()) {
                return createErrorResponse("Role name already exists", ApiConstant.BUSINESS_ERROR.getKey());
            }

            // Create new role
            Role role = new Role();
            role = roleHandlerService.convertRoleRequestToRole(roleRequest, role);
            role.setStatus(ApiConstant.ACTIVE.getKey());
            role = roleRepository.save(role);

            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
        } catch (BusinessException e) {
            log.error("Business error creating role: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error creating role: {}", e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate update(Long id, RoleRequest roleRequest) {
        try {
            // Validate request
            ResponseErrorTemplate validationError = roleHandlerService.roleRequestValidation(roleRequest);
            if (validationError != null && validationError.isError()) {
                return validationError;
            }

            // Find existing role
            Role role = roleRepository.findFirstById(id)
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_ID_NOT_FOUND.getDescription(), id)));

            // Check for duplicate name if name is being updated
            if (roleRequest.name() != null && !roleRequest.name().equals(role.getName())) {
                roleRepository.findFirstByName(roleRequest.name())
                        .ifPresent(existingRole -> {
                            if (!existingRole.getId().equals(id)) {
                                throw new BusinessException("Role name already exists");
                            }
                        });
            }

            // Update role
            role = roleHandlerService.convertRoleRequestToRole(roleRequest, role);
            role.setStatus(roleRequest.status() != null ? roleRequest.status() : role.getStatus());
            role = roleRepository.saveAndFlush(role);

            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
        } catch (BusinessException e) {
            log.error("Business error updating role: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error updating role: {}", e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findById(Long id) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_ID_NOT_FOUND.getDescription(), id)));

            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
        } catch (BusinessException e) {
            log.error("Business error finding role by id {}: {}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error finding role by id {}: {}", id, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findAll(RoleFilterRequest roleFilterRequest) {
        try {
            // Build search criteria
            BaseSearchCriteria baseSearchCriteria = buildSearchCriteria(roleFilterRequest);

            // Get paginated results
            PageableResponseVO<Role> pageResponse = baseEntityService.listPage(
                    Role.class, baseSearchCriteria, roleFilterRequest);

            if (pageResponse == null || pageResponse.getContent().isEmpty()) {
                log.info("No roles found with filter: {}", roleFilterRequest);
                return createErrorResponse(ApiConstant.ROLE_NOT_FOUND.getDescription(), 
                                         ApiConstant.ROLE_NOT_FOUND.getKey());
            }

            // Convert roles to response DTOs
            List<RoleResponse> roleResponses = pageResponse.getContent().stream()
                    .map(roleHandlerService::convertRoleToRoleResponse)
                    .collect(Collectors.toList());

            // Create pagination response
            RolePaginationResponse rolePaginationResponse = new RolePaginationResponse(
                    roleResponses,
                    pageableResponseHandlerService.handlePaginationResponse(
                            pageResponse.getTotalElements(),
                            pageResponse.getPageNumber(),
                            pageResponse.getPageSize()
                    ),
                    Metadata.builder()
                            .hasNext(!pageResponse.isLast() && !pageResponse.isEmpty())
                            .totalUsers(pageResponse.getTotalElements())
                            .hasPrevious(!pageResponse.isFirst() && !pageResponse.isEmpty())
                            .currentPage(pageResponse.getPageNumber())
                            .pageSize(pageResponse.getPageSize())
                            .build()
            );

            return createSuccessResponse(rolePaginationResponse);
        } catch (BusinessException e) {
            log.error("Business error retrieving roles with filter {}: {}", roleFilterRequest, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error retrieving roles with filter {}: {}", roleFilterRequest, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate delete(Long id) {
        try {
            Role role = roleRepository.findFirstById(id)
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_ID_NOT_FOUND.getDescription(), id)));

            roleRepository.delete(role);
            return createSuccessResponse(null);
        } catch (BusinessException e) {
            log.error("Business error deleting role {}: {}", id, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error deleting role {}: {}", id, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate deleteAll(Set<Long> ids) {
        try {
            List<Role> roles = roleRepository.findAllByIdIn(ids);
            if (roles.isEmpty()) {
                return createErrorResponse(ApiConstant.ROLE_NOT_FOUND.getDescription(), 
                                         ApiConstant.ROLE_NOT_FOUND.getKey());
            }

            roleRepository.deleteAll(roles);
            return createSuccessResponse(null);
        } catch (BusinessException e) {
            log.error("Business error deleting roles {}: {}", ids, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error deleting roles {}: {}", ids, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseErrorTemplate findByName(String name) {
        try {
            Role role = roleRepository.findFirstByNameAndStatus(name, ApiConstant.ACTIVE.getKey())
                    .orElseThrow(() -> new BusinessException(
                            String.format(ApiConstant.ROLE_NAME_NOT_FOUND.getDescription(), name)));

            return createSuccessResponse(roleHandlerService.convertRoleToRoleResponse(role));
        } catch (BusinessException e) {
            log.error("Business error finding role by name {}: {}", name, e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error finding role by name {}: {}", name, e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    @Override
    @Transactional
    public ResponseErrorTemplate disActivateRole(Set<Long> ids, String status) {
        try {
            List<Role> roles = roleRepository.findAllByIdIn(ids);
            if (roles.isEmpty()) {
                return createErrorResponse(ApiConstant.ROLE_NOT_FOUND.getDescription(), 
                                         ApiConstant.ROLE_NOT_FOUND.getKey());
            }

            roles.forEach(role -> {
                role.setStatus(Optional.ofNullable(status).orElse(ApiConstant.IN_ACTIVE.getKey()));
                roleRepository.saveAndFlush(role);
            });

            return createSuccessResponse(null);
        } catch (BusinessException e) {
            log.error("Business error deactivating roles: {}", e.getMessage());
            return createErrorResponse(e.getMessage(), ApiConstant.BUSINESS_ERROR.getKey());
        } catch (Exception e) {
            log.error("Unexpected error deactivating roles: {}", e.getMessage());
            return createErrorResponse(ApiConstant.INTERNAL_SERVER_ERROR.getDescription(), 
                                     ApiConstant.INTERNAL_SERVER_ERROR.getKey());
        }
    }

    private BaseSearchCriteria buildSearchCriteria(RoleFilterRequest roleFilterRequest) {
        BaseSearchCriteria baseSearchCriteria = new BaseSearchCriteria();

        if (roleFilterRequest.hasId()) {
            baseSearchCriteria.addCriteria(new SearchCriteria(
                    "id", roleFilterRequest.getId(), SearchOperation.EQUAL));
        }

        if (roleFilterRequest.hasName()) {
            baseSearchCriteria.addCriteria(new SearchCriteria(
                    "name", "%" + roleFilterRequest.getName().toLowerCase() + "%", SearchOperation.MATCH));
        }

        if (roleFilterRequest.hasStatus()) {
            baseSearchCriteria.addCriteria(new SearchCriteria(
                    "status", roleFilterRequest.getStatus(), SearchOperation.EQUAL));
        }

        if (StringUtils.hasText(roleFilterRequest.getSortBy())) {
            baseSearchCriteria.setSortBy(roleFilterRequest.getSortBy());
            baseSearchCriteria.setDesc(roleFilterRequest.isDesc());
        }

        return baseSearchCriteria;
    }

    private ResponseErrorTemplate createSuccessResponse(Object data) {
        return new ResponseErrorTemplate(
                ApiConstant.SUCCESS.getDescription(),
                ApiConstant.SUCCESS.getKey(),
                data,
                false
        );
    }

    private ResponseErrorTemplate createErrorResponse(String message, String code) {
        return new ResponseErrorTemplate(
                message,
                code,
                new Object(),
                true
        );
    }
}
