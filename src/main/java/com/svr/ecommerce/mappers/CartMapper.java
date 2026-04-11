package com.svr.ecommerce.mappers;

import com.svr.ecommerce.dtos.CartDto;
import com.svr.ecommerce.dtos.CartItemDto;
import com.svr.ecommerce.entities.Cart;
import com.svr.ecommerce.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
