package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.CheckoutRequest;
import com.svr.ecommerce.dtos.CheckoutResponse;
import com.svr.ecommerce.entities.Order;
import com.svr.ecommerce.entities.OrderItem;
import com.svr.ecommerce.entities.OrderStatus;
import com.svr.ecommerce.repositories.CartRepository;
import com.svr.ecommerce.repositories.OrderRepository;
import com.svr.ecommerce.services.AuthService;
import com.svr.ecommerce.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckoutController {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if(cart == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Cart not found"));
        if(cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        Order order = new Order();
        order.setCustomer(authService.getCurrentUser());
        order.setStatus(OrderStatus.PENDING);
        order.setTotal_price(cart.getTotalPrice());

        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnit_price(cartItem.getProduct().getPrice());
            orderItem.setTotal_price(cartItem.getTotalPrice());
            order.getItems().add(orderItem);
        });

        orderRepository.save(order);
        cartService.clearItems(request.getCartId());

        return ResponseEntity.ok().body(new CheckoutResponse(order.getId()));
    }
}
