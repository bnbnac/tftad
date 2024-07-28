package com.tftad.service;

import com.tftad.config.data.RefreshRequest;
import com.tftad.config.property.AuthProperty;
import com.tftad.domain.RefreshToken;
import com.tftad.exception.ExpiredToken;
import com.tftad.exception.TokenNotFound;
import com.tftad.exception.UnmatchedToken;
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
        // 로그인 기기 대수 제한을 둬야함
        // uuid 유니크?
        return token;
    }

    private RefreshToken createRefreshToken(Long memberId, String token) {
        return RefreshToken.builder()
                .memberId(memberId)
                .refreshToken(token)
                .durationDays(authProperty.getRefreshTokenCookieMaxAgeInDays())
                .build();
    }

    public void verify(RefreshRequest refreshRequest) {
        RefreshToken foundToken = findRefreshToken(refreshRequest.getMemberId());

        if (foundToken.isExpired()) {
            throw new ExpiredToken();
        }

        if (!foundToken.matches(refreshRequest.getToken())) {
            throw new UnmatchedToken();
        }
    }

    private RefreshToken findRefreshToken(Long memberId) {
        // 개선 필요. 어차피 메타데이터 테이블 만들거라 일단 get(0)로 처리
        RefreshToken foundToken = refreshTokenRepository.findByMemberId(memberId).get(0);

        if (foundToken == null) {
            throw new TokenNotFound();
        }
        return foundToken;
    }

    public void delete(RefreshRequest refreshRequest) {
        RefreshToken foundToken = findRefreshToken(refreshRequest.getMemberId());
        refreshTokenRepository.delete(foundToken);
    }
}
