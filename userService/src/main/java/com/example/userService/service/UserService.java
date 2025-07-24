package com.example.userService.service;


import com.example.userService.client.AuthServiceClient;
import com.example.userService.entity.User;
import com.example.userService.model.LoginModel;
import com.example.userService.repository.UserRepo;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final AuthServiceClient authServiceClient;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    @PostConstruct
    public void createSuperUser() {
        User superuser = userRepo.findByUsername("admin").orElse(null);
        if (superuser == null) {
            superuser = new User();
            superuser.setUsername("admin");
        }
        superuser.setPassword(passwordEncoder.encode("admin@123"));
        userRepo.save(superuser);
    }

    public ResponseEntity<?> createUser(User newUser) {
        Map<String, String> response = new HashMap<>();
        if (userRepo.findByUsername(newUser.getUsername()).isPresent()) {
            log.info("User already exist");
            response.put("Status", "Error");
            response.put("Message", "User with that username already exist");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepo.save(newUser);
        response.put("Status", "Success");
        response.put("Message", "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> findUserByUsername(String username) {
        log.info("Username in findUser {}", username);
        Map<String, String> res = new HashMap<>();
        User userObj = userRepo.findByUsername(username).orElse(null);
        if (userObj != null) {
            return new ResponseEntity<>(userObj, HttpStatus.OK);
        } else {
            res.put("Status", "Error");
            res.put("Message", "User " + username + " not registered yet");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> login(LoginModel userLogin) {
        try {
            ResponseEntity<?> response = authServiceClient.loginUser(userLogin);
            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (FeignException.BadRequest exception) {
            return new ResponseEntity<>(exception.contentUTF8(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}
