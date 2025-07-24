package com.example.authenticationService.controller;

import com.example.authenticationService.model.Login;
import com.example.authenticationService.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Login loginNewUser) {
        log.info("Login api is hit");
        return service.loginUser(loginNewUser);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token) {
        return service.validateAuthToken(token);
    }
}
