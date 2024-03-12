package com.tftad.response;

import com.tftad.domain.Post;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class PostResponse {

    private final int maxTitleLength = 15;

    private final Long id;
    private final String title;
    private final String content;
    private final Boolean published;
    private final String videoId;

    public PostResponse(Post post) {
        Assert.notNull(post, "post must not be null");
        Assert.hasText(post.getTitle(), "title must not be null");
        Assert.hasText(post.getContent(), "content must not be null");
        Assert.notNull(post.getPublished(), "published must not be null");
        Assert.hasText(post.getVideoId(), "videoId must not be null");

        this.id = post.getId();
        this.title = limitTitleLength(post.getTitle(), maxTitleLength);
        this.content = post.getContent();
        this.published = post.getPublished();
        this.videoId = post.getVideoId();
    }

    private String limitTitleLength(String title, int length) {
        return title.substring(0, Math.min(title.length(), length));
    }
}
