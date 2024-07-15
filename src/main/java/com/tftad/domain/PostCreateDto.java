package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class PostCreateDto {

    private final String title;
    private final String content;
    private final String videoId;

    @Builder
    public PostCreateDto(String title, String content, String videoId) {
        Assert.hasText(title, "title must not be null");
        Assert.hasText(content, "content must not be null");
        Assert.hasText(videoId, "videoId must not be null");

        this.title = title;
        this.content = content;
        this.videoId = videoId;
    }
}
