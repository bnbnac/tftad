package com.tftad.response;

import com.tftad.domain.Post;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponseDetail {

    private final PostResponse post;
    private final List<QuestionResponse> questions;
    private final ChannelResponse channel;

    public PostResponseDetail(Post post) {
        Assert.notNull(post, "post must not be null");
        Assert.notNull(post.getId(), "post id must not be null");
        Assert.hasText(post.getTitle(), "title must not be null");
        Assert.hasText(post.getContent(), "content must not be null");
        Assert.notNull(post.getPublished(), "published must not be null");
        Assert.hasText(post.getVideoId(), "videoId must not be null");
        Assert.notNull(post.getCreatedAt(), "created time must not be null");
        Assert.notNull(post.getUpdatedAt(), "updated time must not be null");

        this.post = new PostResponse(post);
        this.questions = post.getQuestions()
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
        this.channel = new ChannelResponse(post.getChannel());
    }
}
