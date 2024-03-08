package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public void saveQuestionsOnThePostFromExtractorResult(Post post, List<String> extractorResult) {
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
