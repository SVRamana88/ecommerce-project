package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.UserDto;
import com.svr.ecommerce.entities.User;
import com.svr.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    //hello world

    private final UserRepository userRepository;

    //@RequestMapping("/users") //by default GET Method User
    @GetMapping("")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map((user -> new UserDto(user.getId(),  user.getName(), user.getEmail())))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if(user == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        }
        //return new ResponseEntity<>(user, HttpStatus.OK);
        return ResponseEntity.ok(new UserDto(user.getId(),  user.getName(), user.getEmail()));
    }
}
