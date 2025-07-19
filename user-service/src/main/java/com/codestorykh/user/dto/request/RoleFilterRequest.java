package com.codestorykh.user.dto.request;

import com.codestorykh.common.dto.PageableRequestVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
public class RoleFilterRequest extends PageableRequestVO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @JsonProperty("status")
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;

    public RoleFilterRequest() {
        super();
        // Set default values
        this.setPageNumber(0);
        this.setPageSize(10);
    }

    public boolean hasId() {
        return id != null;
    }

    public boolean hasName() {
        return name != null && !name.trim().isEmpty();
    }

    public boolean hasStatus() {
        return status != null && !status.trim().isEmpty();
    }
}
