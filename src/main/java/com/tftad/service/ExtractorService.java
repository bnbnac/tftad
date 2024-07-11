package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.response.PositionOfPostResponse;
import org.springframework.http.ResponseEntity;

public interface ExtractorService {

    PositionOfPostResponse getPosition(Long postId);

    void getAnalysis(String videoId, Long memberId, Long postId);

    ResponseEntity<JsonNode> deleteAnalysisByPostId(Long memberId, Long postId);

    ResponseEntity<JsonNode> deleteAnalysisByQuestionFilename(Long memberId, Long postId, String filename);
}
