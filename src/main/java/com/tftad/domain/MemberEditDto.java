package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEditDto {
    private final Long memberId;
    private final String name;
    private final String password;

    @Builder
    public MemberEditDto(Long memberId, String name, String password) {
        Assert.notNull(memberId, "member id must not be null");

        this.memberId = memberId;
        this.name = name;
        this.password = password;
    }
}
