package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.repository.QuestionRepository;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final PostService postService;

    @Transactional
    public void saveQuestionsFromExtractorResult(Long postId, List<String> extractorResult) {
        Assert.notNull(extractorResult, "extractor result must not be null");
        Post post = postService.getPostById(postId);

        for (int i = 0; i < extractorResult.size(); i += 2) {
            Question question = Question.builder()
                    .startTime(extractorResult.get(i))
                    .endTime(extractorResult.get(i + 1))
                    .post(post)
                    .build();

            questionRepository.save(question);
        }
    }
}
