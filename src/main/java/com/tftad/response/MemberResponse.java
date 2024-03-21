package com.tftad.response;

import com.tftad.domain.Member;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MemberResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final List<ChannelResponse> channels;

    public MemberResponse(Member member) {
        Assert.notNull(member, "member must not be null");
        Assert.notNull(member.getId(), "id must not be null");
        Assert.hasText(member.getEmail(), "email must not be null");
        Assert.hasText(member.getName(), "name must not be null");

        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.channels = member.getChannels()
                .stream()
                .map(ChannelResponse::new)
                .collect(Collectors.toList());
    }
}
