package com.tftad.service;

import com.tftad.domain.*;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.QuestionNotFound;
import com.tftad.repository.QuestionRepository;
import com.tftad.response.QuestionResponse;
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

    @Transactional
    public QuestionDeleteDto delete(Long memberId, Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);

        if (!memberId.equals(question.getPost().getMember().getId())) {
            throw new InvalidRequest("questionId", "문제의 작성자만 문제를 삭제할 수 있습니다");
        }
        questionRepository.delete(question);

        return QuestionDeleteDto.builder()
                .filename(question.generateFilename())
                .postId(question.getPost().getId())
                .build();
    }

    public QuestionResponse get(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);
        return new QuestionResponse(question);
    }

    @Transactional
    public QuestionResponse edit(QuestionEditDto questionEditDto) {
        Question question = questionRepository.findById(questionEditDto.getQuestionId())
                .orElseThrow(QuestionNotFound::new);
        if (!questionEditDto.getMemberId().equals(question.getPost().getMember().getId())) {
            throw new InvalidRequest("memberId", "게시글 작성자만 문제를 수정할 수 있습니다");
        }

        QuestionEditor questionEditor = question.toEditorBuilder()
                .authorIntention(questionEditDto.getAuthorIntention())
                .build();
        question.edit(questionEditor);
        questionRepository.save(question);

        return new QuestionResponse(question);
    }
}
