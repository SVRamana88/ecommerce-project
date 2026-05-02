package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.CheckoutRequest;
import com.svr.ecommerce.dtos.CheckoutResponse;
import com.svr.ecommerce.dtos.ErrorDto;
import com.svr.ecommerce.entities.Order;
import com.svr.ecommerce.entities.OrderItem;
import com.svr.ecommerce.entities.OrderStatus;
import com.svr.ecommerce.exceptions.CartEmptyException;
import com.svr.ecommerce.exceptions.CartNotFoundException;
import com.svr.ecommerce.repositories.CartRepository;
import com.svr.ecommerce.repositories.OrderRepository;
import com.svr.ecommerce.services.AuthService;
import com.svr.ecommerce.services.CartService;
import com.svr.ecommerce.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request.getCartId());
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
    }
}
