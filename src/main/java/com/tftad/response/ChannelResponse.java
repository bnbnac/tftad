package com.tftad.response;

import com.tftad.domain.Channel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChannelResponse {

    private final Long id;
    private final String title;
    private final String youtubeChannelId;
    private final String thumbnail;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ChannelResponse(Channel channel) {
        this.id = channel.getId();
        this.title = channel.getChannelTitle();
        this.youtubeChannelId = channel.getYoutubeChannelId();
        this.thumbnail = channel.getThumbnail();
        this.createdAt = channel.getCreatedAt();
        this.updatedAt = channel.getUpdatedAt();
    }
}
