package com.tftad.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDetail {

    private final PostResponse post;
    private final List<QuestionResponse> questions;
    private final ChannelResponse channel;

    public PostResponseDetail(
            PostResponse postResponse,
            List<QuestionResponse> questionResponses,
            ChannelResponse channelResponse) {

        this.post = postResponse;
        this.questions = questionResponses;
        this.channel = channelResponse;
    }
}
