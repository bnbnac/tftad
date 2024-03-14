package com.tftad.response;

import com.tftad.domain.Post;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponseDetail {

    private final Long id;
    private final String title;
    private final String content;
    private final Boolean published;
    private final String videoUrl;
    private final List<QuestionResponse> questions;

    public PostResponseDetail(Post post) {
        Assert.notNull(post, "post must not be null");
        Assert.notNull(post.getId(), "post id must not be null");
        Assert.hasText(post.getTitle(), "title must not be null");
        Assert.hasText(post.getContent(), "content must not be null");
        Assert.notNull(post.getPublished(), "published must not be null");
        Assert.hasText(post.getVideoId(), "videoId must not be null");

        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.published = post.getPublished();
        this.videoUrl = post.generateYoutubeVideoUrl();
        this.questions = post.getQuestions()
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }
}
