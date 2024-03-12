package com.tftad.config.data;

import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthedMember {

    private final Long id;
    private final String authorizationCode;

    @Builder
    public OAuthedMember(Long id, String authorizationCode) {
        Assert.notNull(id, "id must not null");
        Assert.hasText(authorizationCode, "authorizationCode must not be empty");

        this.id = id;
        this.authorizationCode = authorizationCode;
    }
}
