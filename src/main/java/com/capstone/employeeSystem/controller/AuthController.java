package com.capstone.employeeSystem.controller;

import com.capstone.employeeSystem.model.User;
import com.capstone.employeeSystem.security.JwtFilter;
import com.capstone.employeeSystem.security.JwtUtil;
import com.capstone.employeeSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;


    public AuthController(UserService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try{
            return ResponseEntity.ok(userService.registerUser(user.getUsername(),user.getEmail(), user.getPassword()));
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> foundUser = userService.findByUsername(user.getUsername());


        if (foundUser.isPresent()) {
            User existingUser = foundUser.get();
            if (userService.passwordMatches(user.getPassword(), existingUser.getPassword())) {
                String role = existingUser.getRole();
                String token = jwtUtil.generateToken(user.getUsername(), role);

                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401).body("Invalid Credentials");
    }

}