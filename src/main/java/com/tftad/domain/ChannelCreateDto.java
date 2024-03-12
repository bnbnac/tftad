package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class ChannelCreateDto {

    private final Member member;
    private final String channelTitle;
    private final String youtubeChannelId;

    @Builder
    public ChannelCreateDto(Member member, String channelTitle, String youtubeChannelId) {
        Assert.notNull(member, "member must not be null");
        Assert.hasText(channelTitle, "channel title must not be null");
        Assert.hasText(youtubeChannelId, "youtube channel id must not be null");

        this.member = member;
        this.channelTitle = channelTitle;
        this.youtubeChannelId = youtubeChannelId;
    }
}
