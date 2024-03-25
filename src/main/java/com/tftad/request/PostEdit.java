package com.tftad.request;

import com.tftad.domain.PostEditDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostEdit {

    private String title;
    private String content;
    private List<QuestionEdit> questionEdits;

    @Builder
    public PostEdit(String title, String content, List<QuestionEdit> questionEdits) {
        this.title = title;
        this.content = content;
        this.questionEdits = questionEdits;
    }

    public PostEditDto.PostEditDtoBuilder toPostEditDtoBuilder() {
        return PostEditDto.builder()
                .title(title)
                .content(content);
    }
}
