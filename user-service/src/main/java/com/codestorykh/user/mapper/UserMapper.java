package com.codestorykh.user.mapper;

import com.codestorykh.user.dto.request.CreateUserRequestDTO;
import com.codestorykh.user.dto.response.AuthResponse;
import com.codestorykh.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(CreateUserRequestDTO createUserRequestDTO);

    AuthResponse toAuthResponse(User user);

}
