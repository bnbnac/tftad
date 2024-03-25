package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.List;

@Getter
public class PostEditDto {
    private final String title;
    private final String content;
    private final List<String> authorIntentions;
    private final Long memberId;
    private final Long postId;

    @Builder
    public PostEditDto(String title, String content, List<String> authorIntentions, Long memberId, Long postId) {
        Assert.notNull(memberId, "member id must not be null");
        Assert.notNull(postId, "post id must not be null");

        this.title = title;
        this.content = content;
        this.authorIntentions = authorIntentions;
        this.memberId = memberId;
        this.postId = postId;
    }
}
