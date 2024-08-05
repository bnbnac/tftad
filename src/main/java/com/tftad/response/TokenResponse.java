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
        // 국내(높은정밀도): KT GEOIP, WHOIS API from KISA, 국외(도시정도까지): MaxMind GeoIP2
        this.clientIp = token.getClientIp().substring(0, 4);
        this.userAgent = token.getUserAgent();
    }
}
