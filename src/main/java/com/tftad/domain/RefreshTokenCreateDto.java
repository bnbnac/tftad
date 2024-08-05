package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class RefreshTokenCreateDto {

    private static final String DEFAULT_CLIENT_IP = "0.0.0.0";
    private static final String DEFAULT_USER_AGENT = "Unknown";

    private final Long memberId;
    private final String clientIp;
    private final String userAgent;

    @Builder
    public RefreshTokenCreateDto(Long memberId, String clientIp, String userAgent) {
        Assert.notNull(memberId, "memberId must not be null");

        this.memberId = memberId;
        this.clientIp = (clientIp != null) ? clientIp : DEFAULT_CLIENT_IP;
        this.userAgent = (userAgent != null) ? userAgent : DEFAULT_USER_AGENT;
    }
}
