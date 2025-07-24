package com.example.authenticationService.service;

import com.example.authenticationService.client.UserServiceClient;
import com.example.authenticationService.entity.UserTokenDetails;
import com.example.authenticationService.model.Login;
import com.example.authenticationService.model.UserModel;
import com.example.authenticationService.repository.UserTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Autowired
    private ObjectMapper objectMapper;


    private final UserServiceClient userServiceClient;

    @Autowired
    public AuthService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public ResponseEntity<?> loginUser(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            ResponseEntity<?> res = userServiceClient.getUserByUsername(login.getUsername());
            Object obj = res.getBody();
            UserModel user = objectMapper.convertValue(obj, UserModel.class);
            log.info("Username in login is {}", user.getUsername());
            if (encoder.matches(login.getPassword(), user.getPassword())) {
                log.info("User {} authenticated successfully", login.getUsername());
                String token = generateToken(login.getUsername());
                log.info("Generated Token: {}", token);
                UserTokenDetails newUserToken = userTokenRepository.findByUsername(login.getUsername()).orElse(null);
                if (newUserToken == null) {
                    newUserToken = new UserTokenDetails();
                    newUserToken.setUsername(login.getUsername());
                }
                newUserToken.setToken(token);
                newUserToken.setCreatedAt(LocalDateTime.now());
                userTokenRepository.save(newUserToken);
                response.put("Status", "Success");
                response.put("Message", "User Authenticated successfully");
                response.put("Token", token);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("Status", "Error");
                response.put("Message", "Invalid Password, Please provide valid Credentials");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (FeignException.BadRequest exception) {
            return new ResponseEntity<>(exception.contentUTF8(), HttpStatus.BAD_REQUEST);
        }
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 600 * 1000))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateAuthToken(String authToken) {
        try {
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);
                String username = extractUsername(token);
                UserTokenDetails user = userTokenRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    return token.equals(user.getToken()) && !checkExpiration(token);
                }
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
        }
        return false;
    }

    public boolean checkExpiration(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

}
