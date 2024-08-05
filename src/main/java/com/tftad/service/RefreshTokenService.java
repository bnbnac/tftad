package com.tftad.service;

import com.tftad.config.data.RefreshRequest;
import com.tftad.config.property.AuthProperty;
import com.tftad.domain.RefreshToken;
import com.tftad.domain.RefreshTokenCreateDto;
import com.tftad.exception.ExpiredToken;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.TokenNotFound;
import com.tftad.exception.UnmatchedToken;
import com.tftad.repository.RefreshTokenRepository;
import com.tftad.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final AuthProperty authProperty;
    private final RefreshTokenRepository refreshTokenRepository;

    public String save(RefreshTokenCreateDto refreshTokenCreateDto) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = createRefreshToken(refreshTokenCreateDto, token);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    private RefreshToken createRefreshToken(RefreshTokenCreateDto refreshTokenCreateDto, String token) {
        return RefreshToken.builder()
                .memberId(refreshTokenCreateDto.getMemberId())
                .token(token)
                .durationDays(authProperty.getRefreshTokenCookieMaxAgeInDays())
                .clientIp(refreshTokenCreateDto.getClientIp())
                .userAgent(refreshTokenCreateDto.getUserAgent())
                .build();
    }

    public void verify(RefreshRequest refreshRequest) {
        RefreshToken foundToken = findRefreshToken(refreshRequest.getToken());

        if (!foundToken.isOwnedBy(refreshRequest.getMemberId())) {
            throw new InvalidRequest("memberId", "소유자가 아닙니다");
        }

        if (foundToken.isExpired()) {
            throw new ExpiredToken();
        }

        if (!foundToken.matches(refreshRequest.getToken())) {
            throw new UnmatchedToken();
        }
    }

    public RefreshToken findRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(TokenNotFound::new);
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public List<TokenResponse> getListOf(Long memberId) {
        return refreshTokenRepository.findByMemberIdOrderByExpiryDate(memberId).stream()
                .map(TokenResponse::new)
                .collect(Collectors.toList());
    }

    public RefreshToken findRefreshToken(Long tokenId) {
        return refreshTokenRepository.findById(tokenId).orElseThrow(TokenNotFound::new);
    }
}
