package com.example.authenticationService.repository;


import com.example.authenticationService.entity.UserTokenDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenDetails, Integer> {
    Optional<UserTokenDetails> findByUsername(String username);
}
