package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.repository.QuestionRepository;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public void saveQuestionsFromExtractorResult(Post post, List<String> extractorResult) {
        Assert.notNull(post, "post must not be null");
        Assert.notNull(extractorResult, "extractor result must not be null");

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
