package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionEditDto {
    private final String authorIntention;
    private final boolean ownerValidated;

    @Builder
    public QuestionEditDto(String authorIntention, boolean ownerValidated) {
        this.authorIntention = authorIntention;
        this.ownerValidated = ownerValidated;
    }
}
