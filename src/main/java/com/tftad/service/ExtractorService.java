package com.tftad.service;

import com.tftad.domain.Post;

import java.util.List;

public interface ExtractorService {
    Post getPost(Long id);

    void generateQuestions(Post post, List<String> successCuts);

}
