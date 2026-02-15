package com.svr.ecommerce.mappers;

import com.svr.ecommerce.dtos.UserDto;
import com.svr.ecommerce.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}