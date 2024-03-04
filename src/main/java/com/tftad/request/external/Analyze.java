package com.tftad.request.external;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Analyze {
    private String videoId;
    private Long postId;

    @Builder
    public Analyze(String videoId, Long postId) {
        this.videoId = videoId;
        this.postId = postId;
    }
}
