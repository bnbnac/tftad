package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.response.PositionOfPostResponse;

import java.util.List;

public interface ExtractorService {

    void generateQuestions(Long postId, List<String> successCuts);

    PositionOfPostResponse getPosition(Post post);

    void queryAnalysis(String videoId, Long postId);

    boolean validatePostInExtractorCompletion(Long postId);

    Post validatePostBeforeGetPosition(Long memberId, Long postId);
}
