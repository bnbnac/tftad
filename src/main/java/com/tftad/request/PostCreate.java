package com.tftad.request;

import com.tftad.domain.PostCreateDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostCreate {

    @NotBlank(message = "타이틀을 입력해주세요")
    private String title;

    @NotBlank(message = "콘텐츠를 입력해주세요")
    private String content;

    @NotBlank(message = "유튜브 영상 url을 입력해주세요")
    private String videoUrl;

    @Builder
    public PostCreate(String title, String content, String videoUrl) {
        this.title = title;
        this.content = content;
        this.videoUrl = videoUrl;
    }

    public PostCreateDto.PostCreateDtoBuilder toPostCreateDto() {
        return PostCreateDto.builder()
                .title(title)
                .content(content);
    }
}
