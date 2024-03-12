package com.tftad.service;

import com.tftad.response.PositionOfPostResponse;

public interface ExtractorService {

    PositionOfPostResponse getPosition(Long postId);

    void queryAnalysis(String videoId, Long postId);

}
