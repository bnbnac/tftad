package com.tftad.config.data;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class RefreshRequest {

    private final String token;
    private final Long memberId;

    @Builder
    public RefreshRequest(String token, Long memberId) {
        Assert.hasText(token, "token must not be null");
        Assert.notNull(memberId, "memberId must not be null");

        this.token = token;
        this.memberId = memberId;
    }
}
