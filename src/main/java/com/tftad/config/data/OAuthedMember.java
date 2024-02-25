package com.tftad.config.data;

import lombok.Getter;

@Getter
public class OAuthedMember {

    private final long id;
    private final String authorizationCode;

    private OAuthedMember(Builder builder) {
        this.id = builder.id;
        this.authorizationCode = builder.authorizationCode;
    }

    public static Builder builder() {
        return new OAuthedMember.Builder();
    }

    public static class Builder {
        private long id;
        private String authorizationCode = "";

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder authorizationCode(String authorizationCode) {
            this.authorizationCode = authorizationCode;
            return this;
        }

        public OAuthedMember build() {
            return new OAuthedMember(this);
        }
    }
}
