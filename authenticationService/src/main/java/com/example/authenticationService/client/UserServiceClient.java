package com.example.authenticationService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserServiceClient {

    @GetMapping("/user/get-user")
    ResponseEntity<?> getUserByUsername(@RequestParam String username);
}
