package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
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
public class Post {

    private static final String YOUTUBE_URL_PREFIX = "https://youtu.be/";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;

    private String title;

    private String videoId;

    private Boolean published = false;

    @Lob
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @Builder
    public Post(String title, String content, String videoId, Member member, Channel channel) {
        Assert.hasText(title, "title must not be null");
        Assert.hasText(content, "content must not be null");
        Assert.hasText(videoId, "videoId must not be null");
        Assert.notNull(member, "member must not be null");
        Assert.notNull(channel, "channel must not be null");

        this.title = title;
        this.content = content;
        this.videoId = videoId;
        changeMember(member);
        changeChannel(channel);
    }

    public PostEditor.PostEditorBuilder toEditorBuilder() {
        return PostEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
    }

    private void changeMember(Member member) {
        if (this.member != null) {
            this.member.getPosts().remove(this);
        }
        this.member = member;
        member.getPosts().add(this);
    }

    private void changeChannel(Channel channel) {
        if (this.channel != null) {
            this.channel.getPosts().remove(this);
        }
        this.channel = channel;
        channel.getPosts().add(this);
    }

    public String generateYoutubeVideoUrl() {
        return YOUTUBE_URL_PREFIX + videoId;
    }

    public String generateLimitedTitle(int limit) {
        return title.substring(0, Math.min(title.length(), limit));
    }

    public String generateLimitedContent(int limit) {
        return content.substring(0, Math.min(content.length(), limit));
    }

    public void show() {
        this.published = true;
    }

    public void hide() {
        this.published = false;
    }
}
