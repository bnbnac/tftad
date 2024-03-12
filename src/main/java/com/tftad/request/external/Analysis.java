package com.tftad.request.external;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class Analysis {
    private final String videoId;
    private final Long postId;

    @Builder
    public Analysis(String videoId, Long postId) {
        Assert.notNull(postId, "postId must not be null");
        Assert.hasText(videoId, "videoId must not be null");

        this.videoId = videoId;
        this.postId = postId;
    }
}
