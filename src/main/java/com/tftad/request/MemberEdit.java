package com.tftad.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEdit {

    private String name;
    private String password;

    @Builder
    public MemberEdit(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
