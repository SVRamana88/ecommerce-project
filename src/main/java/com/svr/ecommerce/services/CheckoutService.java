package com.svr.ecommerce.services;

import com.svr.ecommerce.dtos.CheckoutResponse;
import com.svr.ecommerce.entities.Order;
import com.svr.ecommerce.exceptions.CartEmptyException;
import com.svr.ecommerce.exceptions.CartNotFoundException;
import com.svr.ecommerce.repositories.CartRepository;
import com.svr.ecommerce.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;

    public CheckoutResponse checkout(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null)
            throw new CartNotFoundException();

        if(cart.isEmpty()) {
            throw new CartEmptyException();
//            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty"));
        }

        Order order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);
        cartService.clearItems(cartId);

        return new CheckoutResponse(order.getId());
    }
}
