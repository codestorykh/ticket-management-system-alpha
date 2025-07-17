package com.codestorykh.user.dto.response;

import com.codestorykh.common.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleResponseDTO extends BaseDTO {

    private Long id;

    private String name;

    private String description;

    private String status;

}
