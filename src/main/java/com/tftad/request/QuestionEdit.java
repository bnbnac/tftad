package com.tftad.request;

import com.tftad.domain.QuestionEditDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionEdit {

    private String authorIntention;

    @Builder
    public QuestionEdit(String authorIntention) {
        this.authorIntention = authorIntention;
    }

    public QuestionEditDto.QuestionEditDtoBuilder toQuestionEditDtoBuilder() {
        return QuestionEditDto.builder()
                .authorIntention(authorIntention);
    }
}
