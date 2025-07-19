package com.codestorykh.common.criteria;

import lombok.Getter;

@Getter
public enum SearchOperation {
    EQUAL("eq"),
    NOT_EQUAL("neq"),
    GREATER_THAN("gt"),
    LESS_THAN("lt"),
    GREATER_THAN_EQUAL("gte"),
    LESS_THAN_EQUAL("lte"),
    MATCH("like"),
    MATCH_START("likeStart"),
    MATCH_END("likeEnd"),
    IN("in"),
    NOT_IN("notIn"),
    IS_NULL("isNull"),
    IS_NOT_NULL("isNotNull"),
    BETWEEN("between");

    private final String operation;

    SearchOperation(String operation) {
        this.operation = operation;
    }

    public static SearchOperation fromString(String operation) {
        if (operation != null) {
            for (SearchOperation op : SearchOperation.values()) {
                if (operation.equalsIgnoreCase(op.getOperation())) {
                    return op;
                }
            }
        }
        return EQUAL;
    }

    public boolean isUnaryOperation() {
        return this == IS_NULL || this == IS_NOT_NULL;
    }

    public boolean isBinaryOperation() {
        return !isUnaryOperation();
    }

    public boolean isCollectionOperation() {
        return this == IN || this == NOT_IN;
    }

    public boolean isRangeOperation() {
        return this == BETWEEN;
    }

    public boolean isStringOperation() {
        return this == MATCH || this == MATCH_START || this == MATCH_END;
    }

    public boolean isNumericOperation() {
        return this == GREATER_THAN || this == LESS_THAN ||
               this == GREATER_THAN_EQUAL || this == LESS_THAN_EQUAL;
    }
}
