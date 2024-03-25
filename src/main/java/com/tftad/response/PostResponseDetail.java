package com.tftad.response;

import com.tftad.domain.Post;
import com.tftad.domain.Question;
import lombok.Getter;

import java.util.Comparator;
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
                .sorted(Comparator.comparing(Question::getStartTime))
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
        this.channel = new ChannelResponse(post.getChannel());
    }
}
