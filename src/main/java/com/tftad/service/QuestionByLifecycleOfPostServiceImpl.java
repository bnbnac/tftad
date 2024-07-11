package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.repository.QuestionRepository;
import com.tftad.request.PostEdit;
import com.tftad.request.QuestionEdit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionByLifecycleOfPostServiceImpl implements QuestionByLifecycleOfPostService {

    private final QuestionService questionService;
    private final QuestionRepository questionRepository;

    @Override
    public void editQuestionsOfPost(Long postId, PostEdit postEdit) {
        List<QuestionEdit> questionEdits = postEdit.getQuestionEdits();

        for (QuestionEdit questionEdit : questionEdits) {
            questionService.editQuestion(questionEdit, postId);
        }
    }

    @Override
    public void deleteQuestionsOfPost(Long postId) {
        List<Question> questions = questionRepository.findByPostId(postId);
        if (questions != null && !questions.isEmpty()) {
            questionRepository.deleteAll(questions);
        }
    }

    @Override
    public void createQuestionsOfPost(Post post, List<String> extractorResult) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < extractorResult.size(); i += 2) {
            Question question = createQuestion(post, extractorResult.get(i), extractorResult.get(i + 1));
            questions.add(question);
        }
        questionRepository.saveAll(questions);
    }

    private Question createQuestion(Post post, String startTime, String endTime) {
        return Question.builder()
                .startTime(startTime)
                .endTime(endTime)
                .post(post)
                .build();
    }
}
