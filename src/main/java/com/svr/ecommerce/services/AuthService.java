package com.svr.ecommerce.services;

import com.svr.ecommerce.entities.User;
import com.svr.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId =  (Long)  authentication.getPrincipal();
        var user = userRepository.findById(userId).orElse(null);
        return user;
    }
}
