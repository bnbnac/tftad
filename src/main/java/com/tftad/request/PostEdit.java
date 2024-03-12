package com.tftad.request;

import com.tftad.domain.PostEditDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostEdit {

    private String title;
    private String content;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public PostEditDto.PostEditDtoBuilder toPostEditDtoBuilder() {
        return PostEditDto.builder()
                .title(title)
                .content(content);
    }
}
