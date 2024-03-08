package com.tftad.config.data;

import lombok.Getter;

@Getter
public class AuthenticatedMember {

    private final Long id;

    private AuthenticatedMember(Builder builder) {
        this.id = builder.id;
    }

    public static Builder builder() {
        return new AuthenticatedMember.Builder();
    }

    public static class Builder {
        private Long id;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthenticatedMember build() {
            return new AuthenticatedMember(this);
        }
    }
}
