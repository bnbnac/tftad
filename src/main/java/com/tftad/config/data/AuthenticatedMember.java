package com.tftad.config.data;

import lombok.Getter;

@Getter
public class AuthenticatedMember {

    private final long id;

    private AuthenticatedMember(Builder builder) {
        this.id = builder.id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long id;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public AuthenticatedMember build() {
            return new AuthenticatedMember(this);
        }
    }
}
