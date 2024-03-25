package com.tftad.request;

import com.tftad.domain.QuestionEditDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class QuestionEdit {

    private Long questionId;
    private String authorIntention;

    @Builder
    public QuestionEdit(Long questionId, String authorIntention) {
        this.questionId = questionId;
        this.authorIntention = authorIntention;
    }

    public QuestionEditDto.QuestionEditDtoBuilder toQuestionEditDtoBuilder() {
        return QuestionEditDto.builder()
                .questionId(questionId)
                .authorIntention(authorIntention);
    }
}
