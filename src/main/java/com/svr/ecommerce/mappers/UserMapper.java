package com.svr.ecommerce.mappers;

import com.svr.ecommerce.dtos.RegisterUserRequest;
import com.svr.ecommerce.dtos.UpdateUserRequest;
import com.svr.ecommerce.dtos.UserDto;
import com.svr.ecommerce.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    void update(UpdateUserRequest request, @MappingTarget User user);
}