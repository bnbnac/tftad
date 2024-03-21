package com.tftad.response;

import com.tftad.domain.Question;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class QuestionResponse {

    private final Long id;
    private final String authorIntention;
    private final Integer startTimeOnSecond;
    private final Integer endTimeOnSecond;
    private final String filename;

    public QuestionResponse(Question question) {
        Assert.notNull(question, "question must not be null");
        Assert.notNull(question.getId(), "id must not be null");
        Assert.hasText(question.getStartTime(), "start time must not be null");
        Assert.hasText(question.getEndTime(), "end time must not be null");

        this.id = question.getId();
        this.authorIntention = question.getAuthorIntention();
        this.startTimeOnSecond = question.getStartTimeOnSecond();
        this.endTimeOnSecond = question.getEndTimeOnSecond();
        this.filename = question.generateFilename();
    }
}
