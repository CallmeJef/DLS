package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByResetToken(String resetToken);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}