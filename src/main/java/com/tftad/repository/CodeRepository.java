package com.tftad.repository;

import com.tftad.domain.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {
    void deleteByCreatedAtBefore(LocalDateTime expiryTime);

    long countByEmail(String email);

    Optional<Code> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<Code> findByCodeAndEmailOrderByCreatedAtDesc(String code, String email);
}
