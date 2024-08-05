package com.tftad.response;

import com.tftad.domain.RefreshToken;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenResponse {

    private final Long id;
    private final LocalDateTime createdAt;
    private final String clientIp;
    private final String userAgent;

    public TokenResponse(RefreshToken token) {
        this.id = token.getId();
        this.createdAt = token.getCreatedAt();
        this.clientIp = token.getClientIp().substring(0, 4); // 이걸 region같은걸로
        this.userAgent = token.getUserAgent();
    }
}
