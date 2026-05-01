package com.svr.ecommerce.services;

import com.svr.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    final private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        System.out.println("UserDetailsService LoadByUserName");
        return new User(user.getEmail(), user.getPassword(), Collections.emptyList());
    }
}


//spring security excepts UsernameNotFoundException
//user Details Service return userservice object it container all spring security USER object (genereal representation of user
//spring secu USER object implement userservice obejct
