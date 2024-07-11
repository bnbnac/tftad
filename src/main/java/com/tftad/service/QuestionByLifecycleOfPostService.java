package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.request.PostEdit;

import java.util.List;

public interface QuestionByLifecycleOfPostService {
    void editQuestionsOfPost(Long postId, PostEdit postEdit);

    void deleteQuestionsOfPost(Long postId);

    void createQuestionsOfPost(Post post, List<String> extractorResult);
}
