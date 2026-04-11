package com.svr.ecommerce.services;

import com.svr.ecommerce.dtos.CartDto;
import com.svr.ecommerce.dtos.CartItemDto;
import com.svr.ecommerce.dtos.UpdateCartItemRequest;
import com.svr.ecommerce.entities.Cart;
import com.svr.ecommerce.exceptions.CartNotFoundException;
import com.svr.ecommerce.exceptions.ProductNotFoundException;
import com.svr.ecommerce.mappers.CartMapper;
import com.svr.ecommerce.repositories.CartRepository;
import com.svr.ecommerce.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private CartMapper cartMapper;

    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) throw new CartNotFoundException();
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) throw new ProductNotFoundException();
        var cartItem = cart.addItem(product);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public CartDto getCart(UUID cartId) {
        var carts = cartRepository.getCartWithItems(cartId).orElse(null);
        if (carts == null) throw new CartNotFoundException();
        return cartMapper.toDto(carts);
    }


    public CartItemDto updateItem(UUID cartId, Long productId, @Valid UpdateCartItemRequest request) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null)
            throw new CartNotFoundException();
        var cartItem = cart.getItem(productId);
        if (cartItem == null)
            throw new ProductNotFoundException();
        cartItem.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public void deleteItem(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null)
            throw new CartNotFoundException();
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

    public void clearItems(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) throw new CartNotFoundException();
        cart.clear();
        cartRepository.save(cart);
    }
}
