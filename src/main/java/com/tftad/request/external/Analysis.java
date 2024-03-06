package com.tftad.request.external;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Analysis {
    private String videoId;
    private Long postId;

    @Builder
    public Analysis(String videoId, Long postId) {
        this.videoId = videoId;
        this.postId = postId;
    }
}
