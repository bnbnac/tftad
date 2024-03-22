package com.tftad.response;

import com.tftad.domain.Question;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QuestionResponse {

    private final Long id;
    private final String authorIntention;
    private final Integer startTimeOnSecond;
    private final Integer endTimeOnSecond;
    private final String filename;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public QuestionResponse(Question question) {
        this.id = question.getId();
        this.authorIntention = question.getAuthorIntention();
        this.startTimeOnSecond = question.getStartTimeOnSecond();
        this.endTimeOnSecond = question.getEndTimeOnSecond();
        this.filename = question.generateFilename();
        this.createdAt = question.getCreatedAt();
        this.updatedAt = question.getUpdatedAt();
    }
}
