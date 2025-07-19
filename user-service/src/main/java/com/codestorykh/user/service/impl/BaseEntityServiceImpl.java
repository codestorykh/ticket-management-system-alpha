package com.codestorykh.user.service.impl;

import com.codestorykh.common.criteria.*;
import com.codestorykh.common.dto.PageableRequestVO;
import com.codestorykh.common.dto.PageableResponseVO;
import com.codestorykh.common.exception.BusinessException;
import com.codestorykh.common.repository.BaseRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("baseEntityService")
public class BaseEntityServiceImpl implements BaseRepository {

    private final EntityManager entityManager;

    private CriteriaBuilder criteriaBuilder;

    public BaseEntityServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private <T> Serializable idValue(T entity) {
        return ((JpaEntityInformation<T, Serializable>) entityInformation(entityManager, entity.getClass())).getId(entity);
    }

    private <T> JpaEntityInformation<T, Serializable> entityInformation(EntityManager em, Class<T> clazz) {
        return (JpaEntityInformation<T, Serializable>) JpaEntityInformationSupport.getEntityInformation(clazz, em);
    }

    @Override
    public <T> BaseCriteria<T> createCriteriaBuilder(Class<T> clazz) {
        try {
            BaseCriteria<T> baseCriteria = new BaseCriteria<>();
            criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
            baseCriteria.setCriteriaBuilder(criteriaBuilder);
            baseCriteria.setCriteriaQuery(criteriaQuery);
            return baseCriteria;
        } catch (Exception e) {
            log.error("Error creating criteria builder for type {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error creating criteria builder: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> list(Class<T> clazz) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            baseCriteria.getCriteriaQuery().select(root);
            TypedQuery<T> query = entityManager.createQuery(baseCriteria.getCriteriaQuery());
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error listing entities of type {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error retrieving entities: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> list(Class<T> clazz, boolean isActivate) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            Predicate predicate = criteriaBuilder.equal(root.get(EntityFieldShare.ACTIVATE.getKey()), isActivate);
            return list(baseCriteria.getCriteriaQuery(), predicate);
        } catch (Exception e) {
            log.error("Error listing active entities of type {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error retrieving active entities: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> list(Class<T> clazz, boolean isAsc, boolean isActivate, String... orders) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            Predicate predicate = criteriaBuilder.equal(root.get(EntityFieldShare.ACTIVATE.getKey()), isActivate);
            
            if (orders != null && orders.length > 0) {
                List<Order> orderList = new ArrayList<>();
                for (String order : orders) {
                    if (isAsc) {
                        orderList.add(criteriaBuilder.asc(root.get(order)));
                    } else {
                        orderList.add(criteriaBuilder.desc(root.get(order)));
                    }
                }
                baseCriteria.getCriteriaQuery().orderBy(orderList);
            }

            return list(baseCriteria.getCriteriaQuery(), predicate);
        } catch (Exception e) {
            log.error("Error listing sorted entities of type {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error retrieving sorted entities: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T getEntityById(Serializable id, Class<T> clazz) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            Predicate predicate = criteriaBuilder.equal(root.get("id"), id);
            baseCriteria.getCriteriaQuery().where(predicate);
            TypedQuery<T> query = entityManager.createQuery(baseCriteria.getCriteriaQuery());
            return Optional.ofNullable(query.getSingleResult())
                    .orElseThrow(() -> new BusinessException("Entity not found with id: " + id));
        } catch (Exception e) {
            log.error("Error retrieving entity of type {} with id {}: {}", clazz.getSimpleName(), id, e.getMessage());
            throw new BusinessException("Error retrieving entity: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public <T> void saveOrUpdate(T entity) {
        try {
            if (idValue(entity) == null) {
                entityManager.persist(entity);
            } else {
                entityManager.merge(entity);
            }
        } catch (Exception e) {
            log.error("Error saving/updating entity of type {}: {}", entity.getClass().getSimpleName(), e.getMessage());
            throw new BusinessException("Error saving/updating entity: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public <T> void saveOrUpdate(List<T> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(this::saveOrUpdate);
        }
    }

    @Override
    @Transactional
    public <T> void delete(T entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            log.error("Error deleting entity of type {}: {}", entity.getClass().getSimpleName(), e.getMessage());
            throw new BusinessException("Error deleting entity: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public <T> void delete(List<T> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(this::delete);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> listByField(String field, Object value, Class<T> clazz) {
        try {
            return listByObject(field, value, clazz);
        } catch (Exception e) {
            log.error("Error listing entities of type {} by field {}: {}", clazz.getSimpleName(), field, e.getMessage());
            throw new BusinessException("Error retrieving entities by field: " + e.getMessage());
        }
    }

    private <T> List<T> listByObject(String field, Object value, Class<T> clazz) {
        BaseCriteria<T> criteria = createCriteriaBuilder(clazz);
        Root<T> root = criteria.getCriteriaQuery().from(clazz);
        Predicate predicate = createPredicate(root, field, value);
        criteria.getCriteriaQuery().where(predicate);
        TypedQuery<T> query = entityManager.createQuery(criteria.getCriteriaQuery());
        return query.getResultList();
    }

    private <T> Predicate createPredicate(Root<T> root, String field, Object value) {
        if (value instanceof Date) {
            return criteriaBuilder.equal(root.get(field), (Date) value);
        } else if (value instanceof String) {
            return criteriaBuilder.equal(root.get(field), (String) value);
        } else if (value instanceof Integer) {
            return criteriaBuilder.equal(root.get(field), (Integer) value);
        } else if (value instanceof Double) {
            return criteriaBuilder.equal(root.get(field), (Double) value);
        } else if (value instanceof Boolean) {
            return criteriaBuilder.equal(root.get(field), (Boolean) value);
        } else {
            return criteriaBuilder.equal(root.get(field), value);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> query(String query, Class<T> clazz) {
        try {
            return entityManager.createNativeQuery(query, clazz).getResultList();
        } catch (Exception e) {
            log.error("Error executing query for type {}: {}", clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error executing query: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> query(String query) {
        try {
            return entityManager.createNativeQuery(query).getResultList();
        } catch (Exception e) {
            log.error("Error executing native query: {}", e.getMessage());
            throw new BusinessException("Error executing native query: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> list(CriteriaQuery<T> criteriaQuery) {
        try {
            TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error executing criteria query: {}", e.getMessage());
            throw new BusinessException("Error executing criteria query: " + e.getMessage());
        }
    }

    private <T> List<T> list(CriteriaQuery<T> criteriaQuery, Predicate predicate) {
        criteriaQuery.where(predicate);
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> T getByField(String field, Object value, Class<T> clazz) {
        List<T> results = listByField(field, value, clazz);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> listByFieldByListValue(String field, List<String> values, Class<T> clazz) {
        try {
            BaseSearchCriteria baseSearchCriteria = new BaseSearchCriteria();
            baseSearchCriteria.addCriteria(new SearchCriteria(field, values, SearchOperation.IN));
            return list(clazz, baseSearchCriteria);
        } catch (Exception e) {
            log.error("Error listing entities of type {} by field {} with values: {}", 
                clazz.getSimpleName(), field, e.getMessage());
            throw new BusinessException("Error retrieving entities by field list: " + e.getMessage());
        }
    }

    @Override
    public <T> List<T> list(Class<T> clazz, BaseSearchCriteria baseSearchCriteria, PageableRequestVO pageable) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> list(Class<T> clazz, BaseSearchCriteria baseSearchCriteria) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            CriteriaBuilder criteriaBuilder = baseCriteria.getCriteriaBuilder();

            if (baseSearchCriteria.getJoinCriteria() != null) {
                getJoin(baseSearchCriteria.getJoinCriteria(), root, criteriaBuilder);
            }

            CriteriaQuery<T> criteriaQuery = baseCriteria.getCriteriaQuery().select(root);
            criteriaQuery.where(toPredicate(root, criteriaBuilder, baseSearchCriteria));
            TypedQuery<T> query = entityManager.createQuery(baseCriteria.getCriteriaQuery());
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error listing entities of type {} with search criteria: {}", 
                clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error retrieving entities with search criteria: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> list(Class<T> clazz, PageableRequestVO pageable) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            CriteriaBuilder criteriaBuilder = baseCriteria.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = baseCriteria.getCriteriaQuery();
            baseCriteria.getCriteriaQuery().select(root);

            if (StringUtils.isNotBlank(pageable.getSortBy())) {
                if (pageable.isDesc()) {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(pageable.getSortBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(pageable.getSortBy())));
                }
            }

            TypedQuery<T> query = entityManager.createQuery(baseCriteria.getCriteriaQuery());
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageNumber());
            return query.getResultList();
        } catch (Exception e) {
            log.error("Error listing entities of type {} with pagination: {}", 
                clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error retrieving entities with pagination: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> PageableResponseVO<T> listPage(Class<T> clazz, PageableRequestVO pageable) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            CriteriaBuilder criteriaBuilder = baseCriteria.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = baseCriteria.getCriteriaQuery();
            baseCriteria.getCriteriaQuery().select(root);

            if (StringUtils.isNotBlank(pageable.getSortBy())) {
                if (pageable.isDesc()) {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(pageable.getSortBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(pageable.getSortBy())));
                }
            }

            TypedQuery<T> query = entityManager.createQuery(baseCriteria.getCriteriaQuery());
            Integer count = query.getResultList().size();
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            
            return PageableResponseVO.of(
                query.getResultList(),
                count,
                pageable.getPageNumber(),
                pageable.getPageSize()
            );
        } catch (Exception e) {
            log.error("Error listing page of entities of type {}: {}", 
                clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error retrieving page of entities: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T> PageableResponseVO<T> listPage(Class<T> clazz, BaseSearchCriteria baseSearchCriteria,
                                              PageableRequestVO pageable) {
        try {
            BaseCriteria<T> baseCriteria = createCriteriaBuilder(clazz);
            Root<T> root = baseCriteria.getCriteriaQuery().from(clazz);
            CriteriaBuilder criteriaBuilder = baseCriteria.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = baseCriteria.getCriteriaQuery();

            if (StringUtils.isNotBlank(pageable.getSortBy())) {
                if (pageable.isDesc()) {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(pageable.getSortBy())));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(pageable.getSortBy())));
                }
            }

            if (baseSearchCriteria.getJoinCriteria() != null) {
                getJoin(baseSearchCriteria.getJoinCriteria(), root, criteriaBuilder);
            }

            CriteriaQuery<T> all = baseCriteria.getCriteriaQuery().select(root);
            all.where(toPredicate(root, criteriaBuilder, baseSearchCriteria));
            TypedQuery<T> query = entityManager.createQuery(baseCriteria.getCriteriaQuery());
            Integer count = query.getResultList().size();
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            
            return PageableResponseVO.of(
                query.getResultList(),
                count,
                pageable.getPageNumber(),
                pageable.getPageSize()
            );
        } catch (Exception e) {
            log.error("Error listing page of entities of type {} with search criteria: {}", 
                clazz.getSimpleName(), e.getMessage());
            throw new BusinessException("Error retrieving page of entities with search criteria: " + e.getMessage());
        }
    }

    private <T> void getJoin(List<JoinCriteria> joinCriteriaList, Root<T> rootEntry, CriteriaBuilder cb) {
        if (joinCriteriaList != null && !joinCriteriaList.isEmpty()) {
            for (JoinCriteria joinCriteria : joinCriteriaList) {
                try {
                    Join<T, ?> branchJoin = joinCriteria.buildJoinCriteria(rootEntry);
                    String[] joinEntityField = joinCriteria.getJoinEntity().split("\\.");
                    if (joinEntityField.length == 2) {
                        branchJoin = branchJoin.join(joinEntityField[1], JoinType.INNER);
                    }
                    if (joinCriteria.getSearchOperation() != null) {
                        applyJoinOperation(branchJoin, joinCriteria, cb);
                    } else {
                        branchJoin.on(cb.equal(branchJoin.get(joinCriteria.getPropertyField()), 
                            joinCriteria.getJointValue()));
                    }
                } catch (Exception e) {
                    log.error("Error applying join criteria: {}", e.getMessage());
                    throw new BusinessException("Error applying join criteria: " + e.getMessage());
                }
            }
        }
    }

    private <T> void applyJoinOperation(Join<T, ?> branchJoin, JoinCriteria joinCriteria, CriteriaBuilder cb) {
        SearchOperation operation = joinCriteria.getSearchOperation();
        Object value = joinCriteria.getJointValue();
        String field = joinCriteria.getPropertyField();

        switch (operation) {
            case GREATER_THAN:
                branchJoin.on(cb.greaterThan(branchJoin.get(field), value.toString()));
                break;
            case LESS_THAN:
                if (value instanceof Date) {
                    branchJoin.on(cb.lessThan(branchJoin.get(field).as(Date.class), (Date) value));
                } else {
                    branchJoin.on(cb.lessThan(branchJoin.get(field), value.toString()));
                }
                break;
            case GREATER_THAN_EQUAL:
                if (value instanceof Date) {
                    branchJoin.on(cb.greaterThanOrEqualTo(branchJoin.get(field).as(Date.class), (Date) value));
                } else {
                    branchJoin.on(cb.greaterThanOrEqualTo(branchJoin.get(field), value.toString()));
                }
                break;
            case LESS_THAN_EQUAL:
                if (value instanceof Date) {
                    branchJoin.on(cb.lessThanOrEqualTo(branchJoin.get(field).as(Date.class), (Date) value));
                } else {
                    branchJoin.on(cb.lessThanOrEqualTo(branchJoin.get(field), value.toString()));
                }
                break;
            case NOT_EQUAL:
                branchJoin.on(cb.notEqual(branchJoin.get(field), value.toString()));
                break;
            case EQUAL:
                branchJoin.on(cb.equal(branchJoin.get(field), value.toString()));
                break;
            case MATCH:
                branchJoin.on(cb.like(cb.lower(branchJoin.get(field)), 
                    "%" + value.toString().toLowerCase() + "%"));
                break;
            case MATCH_END:
                branchJoin.on(cb.like(cb.lower(branchJoin.get(field)), 
                    value.toString().toLowerCase() + "%"));
                break;
            case MATCH_START:
                branchJoin.on(cb.like(cb.lower(branchJoin.get(field)), 
                    "%" + value.toString().toLowerCase()));
                break;
            case IN:
                branchJoin.on(cb.in(branchJoin.get(field)).value(value));
                break;
            case NOT_IN:
                branchJoin.on(cb.not(branchJoin.get(field)).in(value));
                break;
            default:
                branchJoin.on(cb.equal(branchJoin.get(field), value));
        }
    }

    private <T> Expression<?> getRootField(Root<T> root, String field) {
        try {
            String[] items = org.springframework.util.StringUtils.split(field, ".");
            if (items != null && items.length > 1) {
                return root.get(items[0]).get(items[1]);
            }
            return root.get(field);
        } catch (Exception e) {
            log.error("Error getting root field {}: {}", field, e.getMessage());
            throw new BusinessException("Error getting root field: " + e.getMessage());
        }
    }

    private <T> List<Predicate> toPredicateItem(Root<T> root, CriteriaBuilder builder, List<SearchCriteria> list) {
        List<Predicate> predicates = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (SearchCriteria criteria : list) {
                try {
                    predicates.add(createSearchPredicate(root, builder, criteria));
                } catch (Exception e) {
                    log.error("Error creating predicate for criteria {}: {}", criteria, e.getMessage());
                    throw new BusinessException("Error creating predicate: " + e.getMessage());
                }
            }
        }
        return predicates;
    }

    private <T> Predicate createSearchPredicate(Root<T> root, CriteriaBuilder builder, SearchCriteria criteria) {
        SearchOperation operation = criteria.getOperation();
        Object value = criteria.getValue();
        String field = criteria.getKey();

        return switch (operation) {
            case GREATER_THAN -> {
                if (value instanceof Date) {
                    yield builder.greaterThan(root.get(field), (Date) value);
                }
                yield builder.greaterThan(root.get(field), value.toString());
            }
            case LESS_THAN -> {
                if (value instanceof Date) {
                    yield builder.lessThan(root.get(field), (Date) value);
                }
                yield builder.lessThan(root.get(field), value.toString());
            }
            case GREATER_THAN_EQUAL -> {
                if (value instanceof Date) {
                    yield builder.greaterThanOrEqualTo(root.get(field), (Date) value);
                }
                yield builder.greaterThanOrEqualTo(root.get(field), value.toString());
            }
            case LESS_THAN_EQUAL -> {
                if (value instanceof Date) {
                    yield builder.lessThanOrEqualTo(root.get(field), (Date) value);
                }
                yield builder.lessThanOrEqualTo(root.get(field), value.toString());
            }
            case NOT_EQUAL -> {
                if (value instanceof String) {
                    yield builder.notEqual(root.get(field), value.toString());
                } else if (value == null) {
                    yield builder.isNotNull(root.get(field));
                }
                yield builder.notEqual(root.get(field), value);
            }
            case EQUAL -> {
                if (value instanceof Date) {
                    yield builder.equal(root.get(field), (Date) value);
                } else if (value instanceof Boolean) {
                    yield builder.equal(root.get(field), (Boolean) value);
                } else if (value == null) {
                    yield builder.isNull(root.get(field));
                }
                yield builder.equal(root.get(field), value);
            }
            case MATCH -> builder.like(builder.lower(root.get(field)),
                    "%" + value.toString().toLowerCase() + "%");
            case MATCH_END -> builder.like(builder.lower(root.get(field)),
                    value.toString().toLowerCase() + "%");
            case MATCH_START -> builder.like(builder.lower(root.get(field)),
                    "%" + value.toString().toLowerCase());
            case IN -> builder.in(root.get(field)).value(value);
            case NOT_IN -> builder.not(root.get(field)).in(value);
            default -> builder.equal(root.get(field), value);
        };
    }

    private Predicate toPredicate(Root root, CriteriaBuilder builder, BaseSearchCriteria baseSearchCriteria) {
        try {
            List<Predicate> predicates = toPredicateItem(root, builder, baseSearchCriteria.getSearchCriteria());
            List<Predicate> predicatesOR = toPredicateItem(root, builder, baseSearchCriteria.getSearchOrCriteria());
            
            Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
            Predicate[] predicateORArray = predicatesOR.toArray(new Predicate[0]);
            
            Predicate preAnd = builder.and(predicateArray);
            Predicate preOr = builder.or(predicateORArray);
            
            if (baseSearchCriteria.getSearchOrCriteria() != null && !baseSearchCriteria.getSearchOrCriteria().isEmpty()) {
                return builder.and(preAnd, preOr);
            }
            return builder.and(preAnd);
        } catch (Exception e) {
            log.error("Error creating final predicate: {}", e.getMessage());
            throw new BusinessException("Error creating final predicate: " + e.getMessage());
        }
    }
}
