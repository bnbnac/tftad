package com.tftad.response;

import com.tftad.domain.Post;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponseDetail {

    private final PostResponse post;
    private final List<QuestionResponse> questions;
    private final ChannelResponse channel;

    public PostResponseDetail(Post post) {
        this.post = new PostResponse(post);
        this.questions = post.getQuestions()
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
        this.channel = new ChannelResponse(post.getChannel());
    }
}
