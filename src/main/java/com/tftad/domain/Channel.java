package com.tftad.domain;

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

    private String title;

    private String youtubeChannelId;

    @Builder
    public Channel(String title, String youtubeChannelId, Member member) {
        this.title = title;
        this.youtubeChannelId = youtubeChannelId;
        if (member != null) {
            changeMember(member);
        }
    }

    private void changeMember(Member member) {
        if (this.member != null) {
            this.member.getChannels().remove(this);
        }
        this.member = member;
        member.getChannels().add(this);
    }
}
