package com.svr.ecommerce.controllers;

import com.svr.ecommerce.config.JwtConfig;
import com.svr.ecommerce.dtos.JwtResponse;
import com.svr.ecommerce.dtos.LoginRequest;
import com.svr.ecommerce.dtos.UserDto;
import com.svr.ecommerce.mappers.UserMapper;
import com.svr.ecommerce.repositories.UserRepository;
import com.svr.ecommerce.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    final private AuthenticationManager authenticationManager;
    final private JwtService jwtService;
    final private JwtConfig jwtConfig;
    final private UserRepository userRepository;
    final private UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {

       authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
       );

       var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

       var accessToken = jwtService.generateAccessToken(user).toString();
       var refreshToken = jwtService.generateRefreshToken(user).toString();

       var cookie  =  new Cookie("refreshToken", refreshToken);
       cookie.setHttpOnly(true);
       cookie.setPath("/auth/refresh");
       cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
       cookie.setSecure(true);
       response.addCookie(cookie);

       return ResponseEntity.ok(new JwtResponse(accessToken));
    }
//
//    @PostMapping("/validate")
//    public boolean validate(@RequestHeader("Authorization") String authHeader) {
//        System.out.println("Validate Called");
//        var token = authHeader.replace("Bearer ", "");
//        return jwtService.validateToken(token);
//    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user).toString();
        return ResponseEntity.ok(new JwtResponse(accessToken));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        var userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if(user == null) return ResponseEntity.notFound().build();
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
