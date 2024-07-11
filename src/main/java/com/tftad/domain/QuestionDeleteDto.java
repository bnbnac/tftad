package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionDeleteDto {
    private final Long postId;
    private final String filename;

    @Builder
    public QuestionDeleteDto(Long postId, String filename) {
        Assert.notNull(postId, "post id must not be null");
        Assert.hasText(filename, "filename must not be null");

        this.filename = filename;
        this.postId = postId;
    }
}
