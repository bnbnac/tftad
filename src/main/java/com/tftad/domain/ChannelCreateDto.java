package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class ChannelCreateDto {

    private final Long memberId;?
    private final String channelTitle;
    private final String youtubeChannelId;
    private final String thumbnail;

    @Builder
    public ChannelCreateDto(Long memberId, String channelTitle, String youtubeChannelId, String thumbnail) {
        Assert.notNull(memberId, "memberId must not be null");
        Assert.hasText(channelTitle, "channel title must not be null");
        Assert.hasText(youtubeChannelId, "youtube channel id must not be null");

        this.memberId = memberId;
        this.channelTitle = channelTitle;
        this.youtubeChannelId = youtubeChannelId;
        this.thumbnail = thumbnail;
    }
}
