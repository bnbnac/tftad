package com.tftad.response;

import com.tftad.domain.Channel;
import lombok.Getter;
import org.springframework.util.Assert;

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
        Assert.notNull(channel, "channel must not be null");
        Assert.notNull(channel.getId(), "channel id must not be null");
        Assert.notNull(channel.getThumbnail(), "thumbnail must not be null");
        Assert.hasText(channel.getChannelTitle(), "title must not be null");
        Assert.hasText(channel.getYoutubeChannelId(), "youtube channel id must not be null");
        Assert.notNull(channel.getCreatedAt(), "created time must not be null");
        Assert.notNull(channel.getUpdatedAt(), "updated time must not be null");

        this.id = channel.getId();
        this.title = channel.getChannelTitle();
        this.youtubeChannelId = channel.getYoutubeChannelId();
        this.thumbnail = channel.getThumbnail();
        this.createdAt = channel.getCreatedAt();
        this.updatedAt = channel.getUpdatedAt();
    }
}
