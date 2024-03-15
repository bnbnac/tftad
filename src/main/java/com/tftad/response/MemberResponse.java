package com.tftad.response;

import com.tftad.domain.Member;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class MemberResponse {
    private final Long id;
    private final String email;
    private final String name;

    public MemberResponse(Member member) {
        Assert.notNull(member, "member must not be null");
        Assert.notNull(member.getId(), "id must not be null");
        Assert.hasText(member.getEmail(), "email must not be null");
        Assert.hasText(member.getName(), "name must not be null");

        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
    }
}
