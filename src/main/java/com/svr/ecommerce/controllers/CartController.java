package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.*;
import com.svr.ecommerce.exceptions.CartNotFoundException;
import com.svr.ecommerce.exceptions.ProductNotFoundException;
import com.svr.ecommerce.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Carts")
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
    @Operation(summary = "Adds a product to the cart.")
    public ResponseEntity<CartItemDto> addToCart(
            @Parameter(description = "The ID of the cart.")
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
    public ResponseEntity<ErrorDto> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto("Cart not found."));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto("Product was  not found in the cart."));
    }
}
