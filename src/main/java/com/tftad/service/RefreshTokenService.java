package com.tftad.service;

import com.tftad.config.property.AuthProperty;
import com.tftad.domain.RefreshToken;
import com.tftad.exception.Unauthorized;
import com.tftad.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final AuthProperty authProperty;
    private final RefreshTokenRepository refreshTokenRepository;

    public String save(Long memberId) {
        String token = UUID.randomUUID().toString();
        refreshTokenRepository.save(createRefreshToken(memberId, token));

        return token;
    }

    private RefreshToken createRefreshToken(Long memberId, String token) {
        return RefreshToken.builder()
                .memberId(memberId)
                .refreshToken(token)
                .durationDays(authProperty.getRefreshTokenCookieMaxAgeInDays())
                .build();
    }

    public void verify(String refreshToken, Long memberId) {
        RefreshToken foundToken = findRefreshToken(memberId);

        if (foundToken.isExpired() || !foundToken.matches(refreshToken)) {
            throw new Unauthorized();
        }
    }

    private RefreshToken findRefreshToken(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId).orElseThrow(Unauthorized::new);
    }

    public void delete(Long memberId) {
        RefreshToken foundToken = findRefreshToken(memberId);
        refreshTokenRepository.delete(foundToken);
    }
}
