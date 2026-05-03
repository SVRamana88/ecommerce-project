package com.svr.ecommerce.services;

import com.svr.ecommerce.dtos.OrderDto;
import com.svr.ecommerce.entities.User;
import com.svr.ecommerce.exceptions.OrderNotFoundException;
import com.svr.ecommerce.mappers.OrderMapper;
import com.svr.ecommerce.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();
        var orders = orderRepository.getAllByCustomer(user);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrder(Long orderId) {
        var order = orderRepository.findById(orderId).orElse(null);
        if(order == null) throw new OrderNotFoundException();
        var user = authService.getCurrentUser();
        if(!order.isPlacedBy(user)) throw new AccessDeniedException("You don't have access to this order");
        return orderMapper.toDto(order);
    }

}
