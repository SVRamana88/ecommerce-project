package com.svr.ecommerce.payments;

import com.svr.ecommerce.entities.Order;
import com.svr.ecommerce.exceptions.CartEmptyException;
import com.svr.ecommerce.exceptions.CartNotFoundException;
import com.svr.ecommerce.repositories.CartRepository;
import com.svr.ecommerce.repositories.OrderRepository;
import com.svr.ecommerce.services.AuthService;
import com.svr.ecommerce.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;



    @Transactional
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
        try {
            var session = paymentGateway.createCheckoutSession(order);
            cartService.clearItems(cartId);
            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException ex) {
            orderRepository.delete(order);
            throw ex;
        }
    }

    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway.parseWebhookRequest(request)
                .ifPresent(paymentResult -> {
                    var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });
    }
}
