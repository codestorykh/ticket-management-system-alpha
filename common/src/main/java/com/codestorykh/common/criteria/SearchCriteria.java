package com.codestorykh.common.criteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class SearchCriteria implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Search key cannot be blank")
    private String key;
    
    private Object value;
    
    @NotNull(message = "Search operation cannot be null")
    private SearchOperation operation;

    public SearchCriteria() {
    }

    public SearchCriteria(String key, Object value, SearchOperation operation) {
        this.key = key;
        this.value = value;
        this.operation = operation;
    }

    public SearchCriteria(String key, Object value) {
        this(key, value, SearchOperation.EQUAL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchCriteria that = (SearchCriteria) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value) &&
                operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, operation);
    }

    @Override
    public String toString() {
        return String.format("SearchCriteria{key='%s', value=%s, operation=%s}",
                key, value, operation);
    }

    public boolean isValid() {
        return key != null && !key.trim().isEmpty() && operation != null;
    }
}
