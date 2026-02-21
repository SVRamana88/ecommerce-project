package com.svr.ecommerce.controllers;

import com.svr.ecommerce.dtos.RegisterUserRequest;
import com.svr.ecommerce.dtos.UpdateUserRequest;
import com.svr.ecommerce.dtos.UserDto;
import com.svr.ecommerce.entities.User;
import com.svr.ecommerce.mappers.UserMapper;
import com.svr.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    //hello world

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //@RequestMapping("/users") //by default GET Method User
    @GetMapping("")
    public List<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) {
        if(!Set.of("name", "email").contains(sortBy))
            sortBy = "name";

        return userRepository.findAll(Sort.by(sortBy))
                .stream()
                .map((userMapper::toDto))
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
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody RegisterUserRequest request, UriComponentsBuilder uriBuilder) {
        var user = userRepository.save(userMapper.toEntity(request));
        var userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateUserRequest request
            ) {
        var user = userRepository.findById(id).orElse(null);
        if(user == null) return  ResponseEntity.notFound().build();
        userMapper.update(request, user);
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable(name = "id") Long id
    ) {
        var user = userRepository.findById(id).orElse(null);
        if(user == null) return  ResponseEntity.notFound().build();
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
