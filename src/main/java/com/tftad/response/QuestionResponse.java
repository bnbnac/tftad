package com.tftad.response;

import com.tftad.domain.Question;
import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class QuestionResponse {

    private final Long id;
    private final String authorComment;
    private final Integer startTimeOnSecond;
    private final Integer endTimeOnSecond;
    private final String fileName;

    public QuestionResponse(Question question) {
        Assert.notNull(question, "question must not be null");
        Assert.notNull(question.getId(), "id must not be null");
        Assert.hasText(question.getAuthorComment(), "author comment must not be null");
        Assert.hasText(question.getStartTime(), "start time must not be null");
        Assert.hasText(question.getEndTime(), "end time must not be null");

        this.id = question.getId();
        this.authorComment = question.getAuthorComment();
        this.startTimeOnSecond = question.getStartTimeOnSecond();
        this.endTimeOnSecond = question.getEndTimeOnSecond();
        this.fileName = question.generateFilename();
    }
}
