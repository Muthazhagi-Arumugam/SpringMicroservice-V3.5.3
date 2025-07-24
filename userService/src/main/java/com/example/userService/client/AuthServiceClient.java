package com.example.userService.client;

import com.example.userService.model.LoginModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("authentication-service")
public interface AuthServiceClient {

    @PostMapping("/auth/login")
    ResponseEntity<?> loginUser(@RequestBody LoginModel loginNewUSer);
}
