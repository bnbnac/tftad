package com.tftad.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExtractorCompletion {

    @NotNull(message = "postId required")
    private Long postId;

    @NotNull(message = "fill the process_video() result")
    private List<String> result;
}
