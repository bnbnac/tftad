package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class ChannelCreateDto {

    private final String channelTitle;
    private final String youtubeChannelId;
    private final String thumbnail;

    @Builder
    public ChannelCreateDto(String channelTitle, String youtubeChannelId, String thumbnail) {
        Assert.hasText(channelTitle, "channel title must not be null");
        Assert.hasText(youtubeChannelId, "youtube channel id must not be null");

        this.channelTitle = channelTitle;
        this.youtubeChannelId = youtubeChannelId;
        this.thumbnail = thumbnail;
    }
}
