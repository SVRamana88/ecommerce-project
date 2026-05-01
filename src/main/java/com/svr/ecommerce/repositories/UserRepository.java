package com.svr.ecommerce.repositories;

import com.svr.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);


    Optional<User> findByEmail(String email);
}
