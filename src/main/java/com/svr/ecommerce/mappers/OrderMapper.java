package com.svr.ecommerce.mappers;

import com.svr.ecommerce.dtos.OrderDto;
import com.svr.ecommerce.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
