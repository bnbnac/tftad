package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class QuestionEditDto {
    private final Long memberId;
    private final Long questionId;
    private final String authorIntention;

    @Builder
    public QuestionEditDto(String authorIntention, Long memberId, Long questionId) {
        Assert.notNull(memberId, "member id must not be null");
        Assert.notNull(questionId, "question id must not be null");

        this.authorIntention = authorIntention;
        this.memberId = memberId;
        this.questionId = questionId;
    }


}
