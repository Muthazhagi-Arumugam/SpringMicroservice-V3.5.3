package com.example.userService.controller;

import com.example.userService.entity.User;
import com.example.userService.model.LoginModel;
import com.example.userService.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService service;

    @PostMapping("/register-user")
    public ResponseEntity<?> createNewUSer(@RequestBody User user) {
        return service.createUser(user);
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        return service.findUserByUsername(username);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginModel loginModel) {
        return service.login(loginModel);
    }

    @GetMapping("/list-users")
    public List<User> listAllUsers() {
        return service.getAllUsers();
    }
}
