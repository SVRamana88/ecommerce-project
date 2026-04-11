package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.AddItemToCartRequest;
import com.svr.ecommerce.dtos.CartDto;
import com.svr.ecommerce.dtos.CartItemDto;
import com.svr.ecommerce.dtos.UpdateCartItemRequest;
import com.svr.ecommerce.entities.Cart;
import com.svr.ecommerce.entities.CartItem;
import com.svr.ecommerce.mappers.CartMapper;
import com.svr.ecommerce.repositories.CartRepository;
import com.svr.ecommerce.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {
    final private CartRepository cartRepository;
    final private ProductRepository productRepository;
    final private CartMapper cartMapper;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ) {
        var cart = new Cart();
        cartRepository.save(cart);
        var cartDto = cartMapper.toDto(cart);
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request
    ) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) return ResponseEntity.notFound().build();
        var product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        var cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId())).findFirst().orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        }
        cartRepository.save(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartMapper.toDto(cartItem));
    }

    @GetMapping("{cartId}")
    public ResponseEntity<CartDto> getCarts(
            @PathVariable UUID cartId
    ) {
        var carts = cartRepository.getCartWithItems(cartId).orElse(null);
        if (carts == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cartMapper.toDto(carts));
    }


    @PutMapping("{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart not found"));

        var cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId)).findFirst().orElse(null);

        if (cartItem == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "CartItem not found"));

        cartItem.setQuantity(request.getQuantity());

        cartRepository.save(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartMapper.toDto(cartItem));
    }
}
