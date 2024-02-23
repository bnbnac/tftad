package com.tftad.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private String title;

    private String youtubeChannelId;

    @Builder
    public Channel(String title, String youtubeChannelId) {
        this.title = title;
        this.youtubeChannelId = youtubeChannelId;
    }

    @OneToMany(mappedBy = "channel")
    private List<Question> questions = new ArrayList<>();

    public void changeMember(Member member) {
        this.member = member;
        member.getChannels().add(this);
    }
}
