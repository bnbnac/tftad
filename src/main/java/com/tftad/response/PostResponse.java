package com.tftad.response;

import com.tftad.domain.Post;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private static final int MAX_TITLE_LENGTH = 15;
    private static final int MAX_CONTENT_LENGTH = 15;

    private final Long id;
    private final String title;
    private final String content;
    private final Boolean published;
    private final String videoUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponse(Post post) {
        Assert.notNull(post, "post must not be null");
        Assert.notNull(post.getId(), "post id must not be null");
        Assert.hasText(post.getTitle(), "title must not be null");
        Assert.hasText(post.getContent(), "content must not be null");
        Assert.notNull(post.getPublished(), "published must not be null");
        Assert.hasText(post.getVideoId(), "videoId must not be null");
        Assert.notNull(post.getCreatedAt(), "created time must not be null");
        Assert.notNull(post.getUpdatedAt(), "updated time must not be null");

        this.id = post.getId();
        this.title = post.generateLimitedTitle(MAX_TITLE_LENGTH);
        this.content = post.generateLimitedContent(MAX_CONTENT_LENGTH);
        this.published = post.getPublished();
        this.videoUrl = post.generateYoutubeVideoUrl();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
