package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private String thumbnail;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "channel")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Channel(String channelTitle, String youtubeChannelId, String thumbnail, Member member) {
        Assert.hasText(channelTitle, "channelTitle must not be null");
        Assert.hasText(youtubeChannelId, "youtubeChannelId must not be null");
        Assert.notNull(thumbnail, "thumbnail must not be null(google may serve)");
        Assert.notNull(member, "member must not be null");

        this.channelTitle = channelTitle;
        this.youtubeChannelId = youtubeChannelId;
        this.thumbnail = thumbnail;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        changeMember(member);
    }

    private void changeMember(Member member) {
        if (this.member != null) {
            this.member.getChannels().remove(this);
        }
        this.member = member;
        member.getChannels().add(this);
    }

    public void inherit(Member inheritedMember) {
        this.member = inheritedMember;
    }

    public boolean isOwnedBy(Long memberId) {
        if (memberId == null) {
            return false;
        }
        return memberId.equals(member.getId());
    }
}
