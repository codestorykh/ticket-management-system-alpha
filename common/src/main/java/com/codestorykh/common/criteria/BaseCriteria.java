package com.codestorykh.common.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseCriteria<T> {
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<T> criteriaQuery;
    private Root<T> root;

    public BaseCriteria() {
    }

    public void setRoot(Root<T> root) {
        this.root = root;
        if (criteriaQuery != null) {
            criteriaQuery.select(root);
        }
    }

    public void setCriteriaQuery(CriteriaQuery<T> criteriaQuery) {
        this.criteriaQuery = criteriaQuery;
        if (root != null) {
            criteriaQuery.select(root);
        }
    }

    public void clear() {
        criteriaBuilder = null;
        criteriaQuery = null;
        root = null;
    }

    public boolean isValid() {
        return criteriaBuilder != null && criteriaQuery != null && root != null;
    }
}
