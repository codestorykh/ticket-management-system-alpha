package com.codestorykh.common.criteria;

import com.codestorykh.common.dto.PageableRequestVO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



@Getter
@Setter
@ToString
public class BaseSearchCriteria implements Serializable {

    @Serial
    private static final long serialVersionUID = 460908657376018235L;

    private List<SearchCriteria> searchCriteria;
    private List<SearchCriteria> searchOrCriteria;
    private List<JoinCriteria> joinCriteria;
    private String sortBy;
    private boolean isDesc;

    PageableRequestVO pageableRequestVO;

    private static final Logger log = LoggerFactory.getLogger(BaseSearchCriteria.class);

    public BaseSearchCriteria() {
        this.searchCriteria = new ArrayList<>();
        this.searchOrCriteria = new ArrayList<>();
        this.joinCriteria = new ArrayList<>();
    }

    public void addCriteria(SearchCriteria criteria) {
        if (searchCriteria == null) {
            searchCriteria = new ArrayList<>();
        }
        searchCriteria.add(criteria);
    }

    public void addOrCriteria(SearchCriteria criteria) {
        if (searchOrCriteria == null) {
            searchOrCriteria = new ArrayList<>();
        }
        searchOrCriteria.add(criteria);
    }

    public void addJoinCriteria(JoinCriteria criteria) {
        if (joinCriteria == null) {
            joinCriteria = new ArrayList<>();
        }
        joinCriteria.add(criteria);
    }

    public void clear() {
        if (searchCriteria != null) {
            searchCriteria.clear();
        }
        if (searchOrCriteria != null) {
            searchOrCriteria.clear();
        }
        if (joinCriteria != null) {
            joinCriteria.clear();
        }
        sortBy = null;
        isDesc = false;
    }

    public boolean hasSearchCriteria() {
        return searchCriteria != null && !searchCriteria.isEmpty();
    }

    public boolean hasOrCriteria() {
        return searchOrCriteria != null && !searchOrCriteria.isEmpty();
    }

    public boolean hasJoinCriteria() {
        return joinCriteria != null && !joinCriteria.isEmpty();
    }

    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    public Predicate getPredicate(CriteriaBuilder criteriaBuilder, Root<?> root) {
        try {
            List<Predicate> predicates = new ArrayList<>();

            // Handle search criteria
            if (hasSearchCriteria()) {
                for (SearchCriteria criteria : searchCriteria) {
                    predicates.add(buildPredicate(criteriaBuilder, root, criteria));
                }
            }

            // Handle join criteria
            if (hasJoinCriteria()) {
                for (JoinCriteria joinCriteria : joinCriteria) {
                    Join<?, ?> join = root.join(joinCriteria.getJoinEntity(), JoinType.INNER);
                    // Convert JoinCriteria to SearchCriteria for predicate building
                    SearchCriteria criteria = new SearchCriteria(
                        joinCriteria.getPropertyField(),
                        joinCriteria.getJointValue(),
                        joinCriteria.getSearchOperation()
                    );
                    predicates.add(buildPredicate(criteriaBuilder, join, criteria));
                }
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        } catch (Exception e) {
            log.error("Error building predicate: {}", e.getMessage());
            throw new RuntimeException("Error building search predicate", e);
        }
    }

    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, jakarta.persistence.criteria.From<?, ?> from, SearchCriteria criteria) {
        switch (criteria.getOperation()) {
            case EQUAL:
                return criteriaBuilder.equal(from.get(criteria.getKey()), criteria.getValue());
            case NOT_EQUAL:
                return criteriaBuilder.notEqual(from.get(criteria.getKey()), criteria.getValue());
            case MATCH:
                return criteriaBuilder.like(
                    criteriaBuilder.lower(from.get(criteria.getKey())),
                    "%" + criteria.getValue().toString().toLowerCase() + "%"
                );
            case MATCH_START:
                return criteriaBuilder.like(
                    criteriaBuilder.lower(from.get(criteria.getKey())),
                    criteria.getValue().toString().toLowerCase() + "%"
                );
            case MATCH_END:
                return criteriaBuilder.like(
                    criteriaBuilder.lower(from.get(criteria.getKey())),
                    "%" + criteria.getValue().toString().toLowerCase()
                );
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(from.get(criteria.getKey()), criteria.getValue().toString());
            case GREATER_THAN_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(from.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return criteriaBuilder.lessThan(from.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(from.get(criteria.getKey()), criteria.getValue().toString());
            case IN:
                return from.get(criteria.getKey()).in(criteria.getValue());
            case NOT_IN:
                return from.get(criteria.getKey()).in(criteria.getValue()).not();
            case IS_NULL:
                return criteriaBuilder.isNull(from.get(criteria.getKey()));
            case IS_NOT_NULL:
                return criteriaBuilder.isNotNull(from.get(criteria.getKey()));
            case BETWEEN:
                Object[] values = (Object[]) criteria.getValue();
                return criteriaBuilder.between(
                    from.get(criteria.getKey()),
                    values[0].toString(),
                    values[1].toString()
                );
            default:
                log.warn("Unsupported search operation: {}", criteria.getOperation());
                return null;
        }
    }
}
