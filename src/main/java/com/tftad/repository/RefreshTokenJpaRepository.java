package com.tftad.repository;

import com.tftad.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findByMemberId(Long memberId);
}
