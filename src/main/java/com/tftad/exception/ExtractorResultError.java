package com.tftad.exception;

import lombok.Getter;

@Getter
public class ExtractorResultError extends RuntimeException {

    private final Long memberId;
    private final Long postId;

    public ExtractorResultError(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }

}
