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
public class Post {

    private static final String YOUTUBE_URL_PREFIX = "https://youtu.be/";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    private Long memberId;

    private Long channelId;

    private String title;

    private String videoId;

    private Boolean published = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Lob
    private String content;

    @Builder
    public Post(String title, String content, String videoId, Long memberId, Long channelId) {
        Assert.hasText(title, "title must not be null");
        Assert.hasText(content, "content must not be null");
        Assert.hasText(videoId, "videoId must not be null");
        Assert.notNull(memberId, "memberId must not be null");
        Assert.notNull(channelId, "channelId must not be null");

        this.title = title;
        this.content = content;
        this.videoId = videoId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.memberId = memberId;
        this.channelId = channelId;
    }

    public PostEditor.PostEditorBuilder toEditorBuilder() {
        return PostEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
        this.updatedAt = LocalDateTime.now();
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

    public void publish() {
        this.published = true;
    }

    public void hide() {
        this.published = false;
    }

    public void inherit(Long inheritedMemberId) {
        this.memberId = inheritedMemberId;
    }


    public boolean isOwnedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }
}
