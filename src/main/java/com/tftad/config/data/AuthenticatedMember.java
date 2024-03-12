package com.tftad.config.data;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class AuthenticatedMember {

    private final Long id;

    @Builder
    public AuthenticatedMember(Long id) {
        Assert.notNull(id, "id must not null");

        this.id = id;
    }
}
