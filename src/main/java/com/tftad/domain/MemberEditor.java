package com.tftad.domain;

import lombok.Getter;

@Getter
public class MemberEditor {

    private String name;
    private String password;

    private MemberEditor(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public static MemberEditorBuilder builder() {
        return new MemberEditorBuilder();
    }

    public static class MemberEditorBuilder {
        private String name;
        private String password;

        MemberEditorBuilder() {}

        public MemberEditorBuilder name(String name) {
            if (name != null) {
                this.name = name;
            }
            return this;
        }

        public MemberEditorBuilder password(String password) {
            if (password != null) {
                this.password = password;
            }
            return this;
        }

        public MemberEditor build() {
            return new MemberEditor(name, password);
        }
    }
}
