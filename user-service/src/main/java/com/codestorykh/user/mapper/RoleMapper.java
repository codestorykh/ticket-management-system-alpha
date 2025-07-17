package com.codestorykh.user.mapper;


import com.codestorykh.user.dto.request.CreateRoleRequestDTO;
import com.codestorykh.user.dto.response.RoleResponseDTO;
import com.codestorykh.user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    Role toRole(CreateRoleRequestDTO createRoleRequestDTO);

    RoleResponseDTO toCreateRoleResponseDTO(Role role);
}
