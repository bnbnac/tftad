package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESH_TOKEN_ID")
    private Long id;

    @Column(unique = true)
    private String token;

    private Long memberId;

    private LocalDateTime createdAt;

    private LocalDateTime expiryDate;

    private String clientIp;

    private String userAgent;

    @Builder
    public RefreshToken(Long memberId, String token, Long durationDays, String clientIp, String userAgent) {
        Assert.notNull(memberId, "memberId must not be null");
        Assert.hasText(token, "token must not be null");
        Assert.notNull(durationDays, "durationDays must not be null");

        this.memberId = memberId;
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiryDate = LocalDateTime.now().plusDays(durationDays);
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }

    public boolean matches(String receivedToken) {
        return token.equals(receivedToken);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isOwnedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }
}
