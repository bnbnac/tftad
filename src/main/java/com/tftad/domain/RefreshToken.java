package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESH_TOKEN_ID")
    private Long id;

    private Long memberId;

    private String refreshToken;

    private LocalDateTime expiryDate;

    @Builder
    public RefreshToken(Long memberId, String refreshToken, Long durationDays) {
        Assert.notNull(memberId, "memberId must not be null");
        Assert.hasText(refreshToken, "refreshToken must not be null");
        Assert.notNull(durationDays, "durationDays must not be null");

        this.memberId = memberId;
        this.refreshToken = refreshToken;
        this.expiryDate = LocalDateTime.now().plus(durationDays, ChronoUnit.DAYS);
    }

    public boolean matches(String receivedToken) {
        return refreshToken.equals(receivedToken);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
