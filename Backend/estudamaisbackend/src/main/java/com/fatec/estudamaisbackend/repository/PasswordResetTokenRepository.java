package com.fatec.estudamaisbackend.repository;

import com.fatec.estudamaisbackend.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    void deleteByExpiresAtBefore(Instant when);
}