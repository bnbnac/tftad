package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.response.PositionOfPostResponse;
import org.springframework.http.ResponseEntity;

public interface ExtractorService {

    PositionOfPostResponse getPosition(Long postId);

    void queryAnalysis(String videoId, Long postId);

    ResponseEntity<JsonNode> queryDelete(Long postId);
}
