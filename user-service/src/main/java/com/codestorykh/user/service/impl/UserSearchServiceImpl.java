package com.codestorykh.user.service.impl;

import com.codestorykh.common.criteria.BaseSearchCriteria;
import com.codestorykh.common.criteria.JoinCriteria;
import com.codestorykh.common.criteria.SearchCriteria;
import com.codestorykh.common.criteria.SearchOperation;
import com.codestorykh.common.dto.PageableRequestVO;
import com.codestorykh.common.dto.PageableResponseVO;
import com.codestorykh.common.repository.BaseRepository;
import com.codestorykh.user.dto.request.UserFilterRequest;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.service.UserSearchService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public PageableResponseVO<User> searchUsers(UserFilterRequest filterRequest) {
        try {
            BaseSearchCriteria searchCriteria = buildSearchCriteria(filterRequest);
            return searchUsersWithCriteria(searchCriteria, filterRequest);
        } catch (Exception e) {
            log.error("Error searching users with filter {}: {}", filterRequest, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponseVO<User> searchUsersWithCriteria(BaseSearchCriteria searchCriteria, PageableRequestVO pageable) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            // Apply search criteria
            if (searchCriteria.hasSearchCriteria()) {
                criteriaQuery.where(searchCriteria.getPredicate(criteriaBuilder, root));
            }

            // Apply sorting
            if (pageable.hasSorting()) {
                criteriaQuery.orderBy(pageable.isDesc() 
                    ? criteriaBuilder.desc(root.get(pageable.getSortBy()))
                    : criteriaBuilder.asc(root.get(pageable.getSortBy())));
            }

            // Create and execute query
            TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
            
            // Get total count
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<User> countRoot = countQuery.from(User.class);
            countQuery.select(criteriaBuilder.count(countRoot));
            if (searchCriteria.hasSearchCriteria()) {
                countQuery.where(searchCriteria.getPredicate(criteriaBuilder, countRoot));
            }
            Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

            // Apply pagination
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            // Execute query and get results
            List<User> results = query.getResultList();

            // Create response using the new PageableResponseVO.of() method
            return PageableResponseVO.of(
                results,
                totalCount.intValue(),
                pageable.getPageNumber(),
                pageable.getPageSize()
            );
        } catch (Exception e) {
            log.error("Error searching users with criteria: {}", e.getMessage());
            throw e;
        }
    }

    private BaseSearchCriteria buildSearchCriteria(UserFilterRequest filterRequest) {
        BaseSearchCriteria criteria = new BaseSearchCriteria();

        try {
            // Name search - case insensitive partial match
            if (filterRequest.hasNameFilter()) {
                criteria.addCriteria(new SearchCriteria(
                    "name",
                    "%" + filterRequest.getName().toLowerCase() + "%",
                    SearchOperation.MATCH
                ));
            }

            // Username search - case insensitive exact match
            if (filterRequest.hasUsernameFilter()) {
                criteria.addCriteria(new SearchCriteria(
                    "username",
                    filterRequest.getUsername().toLowerCase(),
                    SearchOperation.EQUAL
                ));
            }

            // Email search - case insensitive partial match
            if (filterRequest.hasEmailFilter()) {
                criteria.addCriteria(new SearchCriteria(
                    "email",
                    "%" + filterRequest.getEmail().toLowerCase() + "%",
                    SearchOperation.MATCH
                ));
            }

            // Status search - case insensitive exact match
            if (filterRequest.hasStatusFilter()) {
                criteria.addCriteria(new SearchCriteria(
                    "status",
                    filterRequest.getStatus().toUpperCase(),
                    SearchOperation.EQUAL
                ));
            }

            // Role search - case insensitive exact match with join
            if (filterRequest.hasRoleFilter()) {
                JoinCriteria joinCriteria = new JoinCriteria();
                joinCriteria.setJoinEntity("roles");
                joinCriteria.setPropertyField("name");
                joinCriteria.setJointValue(filterRequest.getRole().toUpperCase());
                joinCriteria.setSearchOperation(SearchOperation.EQUAL);
                criteria.addJoinCriteria(joinCriteria);
            }

            // Date range search with validation
            if (filterRequest.hasDateRange()) {
                if (filterRequest.getStartDate().isAfter(filterRequest.getEndDate())) {
                    throw new IllegalArgumentException("Start date cannot be after end date");
                }
                
                criteria.addCriteria(new SearchCriteria(
                    "createdAt",
                    filterRequest.getStartDate(),
                    SearchOperation.GREATER_THAN_EQUAL
                ));
                criteria.addCriteria(new SearchCriteria(
                    "createdAt",
                    filterRequest.getEndDate(),
                    SearchOperation.LESS_THAN_EQUAL
                ));
            }

            return criteria;
        } catch (Exception e) {
            log.error("Error building search criteria: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid search criteria: " + e.getMessage());
        }
    }
} 