package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHANNEL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private String channelTitle;

    private String youtubeChannelId;

    @Builder
    public Channel(String channelTitle, String youtubeChannelId, Member member) {
        Assert.hasText(channelTitle, "channelTitle must not be null");
        Assert.hasText(youtubeChannelId, "youtubeChannelId must not be null");
        Assert.notNull(member, "member must not be null");

        this.channelTitle = channelTitle;
        this.youtubeChannelId = youtubeChannelId;
        changeMember(member);
    }

    private void changeMember(Member member) {
        if (this.member != null) {
            this.member.getChannels().remove(this);
        }
        this.member = member;
        member.getChannels().add(this);
    }
}
