package com.tftad.response;

import com.tftad.domain.Member;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MemberResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<ChannelResponse> channels;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.createdAt = member.getCreatedAt();
        this.updatedAt = member.getUpdatedAt();
        this.channels = member.getChannels()
                .stream()
                .map(ChannelResponse::new)
                .collect(Collectors.toList());
    }
}
