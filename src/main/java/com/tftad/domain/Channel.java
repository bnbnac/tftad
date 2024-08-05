package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHANNEL_ID")
    private Long id;

    private Long memberId;

    private String channelTitle;

    private String youtubeChannelId;

    private String thumbnail;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Channel(String channelTitle, String youtubeChannelId, String thumbnail, Long memberId) {
        Assert.hasText(channelTitle, "channelTitle must not be null");
        Assert.hasText(youtubeChannelId, "youtubeChannelId must not be null");
        Assert.notNull(thumbnail, "thumbnail must not be null(google may serve)");
        Assert.notNull(memberId, "member id must not be null");

        this.channelTitle = channelTitle;
        this.youtubeChannelId = youtubeChannelId;
        this.thumbnail = thumbnail;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.memberId = memberId;
    }

    public void inherit(Long inheritedMemberId) {
        this.memberId = inheritedMemberId;
    }

    public boolean isOwnedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }
}
