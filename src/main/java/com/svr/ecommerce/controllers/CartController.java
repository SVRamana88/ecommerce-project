package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.AddItemToCartRequest;
import com.svr.ecommerce.dtos.CartDto;
import com.svr.ecommerce.dtos.CartItemDto;
import com.svr.ecommerce.dtos.UpdateCartItemRequest;
import com.svr.ecommerce.exceptions.CartNotFoundException;
import com.svr.ecommerce.exceptions.ProductNotFoundException;
import com.svr.ecommerce.services.CartService;
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
    final private CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ) {
        var cartDto = cartService.createCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequest request
    ) {
        var cartItemdto = cartService.addToCart(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemdto);
    }

    @GetMapping("{cartId}")
    public CartDto getCarts(
            @PathVariable UUID cartId
    ) {
        return cartService.getCart(cartId);
    }


    @PutMapping("{cartId}/items/{productId}")
    public CartItemDto updateItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(cartId, productId, request);
    }

    @DeleteMapping("{cartId}/items/{productId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId
    ) {
        cartService.deleteItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{cartId}/items")
    public ResponseEntity<?> clearItems(
            @PathVariable UUID cartId
    ) {
        cartService.clearItems(cartId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cart not found."));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Product was  not found in the cart."));
    }
}
