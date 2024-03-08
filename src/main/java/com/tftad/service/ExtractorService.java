package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.response.PositionOfPostResponse;

public interface ExtractorService {

    PositionOfPostResponse getPosition(Post post);

    void queryAnalysis(String videoId, Long postId);

}
