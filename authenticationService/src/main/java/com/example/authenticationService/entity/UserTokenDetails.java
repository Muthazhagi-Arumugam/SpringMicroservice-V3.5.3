package com.example.authenticationService.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "UserTokenDetails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String token;
    private LocalDateTime createdAt;
}
