package com.tftad.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExtractorCompletion {
    private Long postId;
    private List<String> result;
}
