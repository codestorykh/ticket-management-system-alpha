package com.codestorykh.common.criteria;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class JoinCriteria<E> implements Serializable {

    private String alias;
    private String propertyField;
    private String  joinEntity;
    private JoinType joinType;
    private Object jointValue;
    private SearchOperation searchOperation;

    public JoinCriteria() {
    }

    public JoinCriteria(String joinEntity, String propertyField, Object jointValue, JoinType joinType) {
        this.propertyField = propertyField;
        this.joinEntity = joinEntity;
        this.joinType = joinType;
        this.jointValue = jointValue;
    }

    public JoinCriteria(String joinEntity, String propertyField, Object jointValue, JoinType joinType, SearchOperation searchOperation) {
        this.propertyField = propertyField;
        this.joinEntity = joinEntity;
        this.joinType = joinType;
        this.jointValue = jointValue;
        this.searchOperation = searchOperation;
    }

   /* public Join buildJoinCriteria(Root<?> rootEntry) {
    	String entity = joinEntity;
    	if(joinEntity.split("\\.") != null) {
    		String[] arr = joinEntity.split("\\.");
    		entity = arr[0];
    	}
        return rootEntry.join(entity,joinType);
    }*/

    // To this method
    public Join<?, ?> buildJoinCriteria(Root<?> rootEntry) {
        String entity = joinEntity.contains(".") ? joinEntity.split("\\.")[0] : joinEntity;
        return rootEntry.join(entity, joinType);
    }

}
