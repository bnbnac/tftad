package com.tftad.request;

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
}
