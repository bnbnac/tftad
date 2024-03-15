package com.tftad.request;

import com.tftad.domain.MemberEditDto;
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

    public MemberEditDto.MemberEditDtoBuilder toMemberEditDtoBuilder() {
        return MemberEditDto.builder()
                .name(name)
                .password(password);
    }
}
