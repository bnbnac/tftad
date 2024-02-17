package com.tftad.config.data;

import lombok.Getter;

@Getter
public class AuthenticatedMember {

    private final long id;
    private final boolean oAuthed;
    private final String authorizationCode;

    private AuthenticatedMember(Builder builder) {
        this.id = builder.id;
        this.oAuthed = builder.oAuthed;
        this.authorizationCode = builder.authorizationCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long id;
        private boolean oAuthed = false;
        private String authorizationCode = "";

        public Builder id(String id) {
            this.id = Long.parseLong(id);
            return this;
        }

        public Builder authorizationCode(String authorizationCode) {
            if (authorizationCode != null && !authorizationCode.isEmpty()) {
                this.authorizationCode = authorizationCode;
                this.oAuthed = true;
            }
            return this;
        }

        public AuthenticatedMember build() {
            return new AuthenticatedMember(this);
        }
    }
}
