package com.tftad.service;

import com.tftad.domain.*;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.PostNotFound;
import com.tftad.exception.QuestionNotFound;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import com.tftad.request.QuestionEdit;
import com.tftad.response.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final PostRepository postRepository;

    @Transactional
    public void saveQuestionsFromExtractorResult(Long postId, List<String> extractorResult) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

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
            throw new InvalidRequest("questionId", "소유자가 아닙니다");
        }
        questionRepository.delete(question); 멤버와 포스트에서도 없애야

        return QuestionDeleteDto.builder()
                .filename(question.generateFilename())
                .postId(question.getPost().getId())
                .build();
    }

    @Transactional
    public QuestionResponse get(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);
        return new QuestionResponse(question);
    }

    @Transactional
    public List<QuestionResponse> getListOf(Long postId) {
        return questionRepository.findByPostIdOrderByStartTimeAsc(postId)
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long questionId, QuestionEdit questionEdit, Long memberId) {
        Question question = findQuestion(questionId);
        validateQuestionOwner(memberId, question);
        QuestionEditor questionEditor = createEditor(questionEdit, question);
        question.edit(questionEditor);
        questionRepository.save(question);
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);
    }

    private void validateQuestionOwner(Long memberId, Question question) {
        if (!question.getPost().isOwnedBy(memberId)) {
            throw new InvalidRequest("memberId", "소유자가 아닙니다");
        }
    }

    private QuestionEditor createEditor(QuestionEdit questionEdit, Question question) {
        return question.toEditorBuilder()
                .authorIntention(questionEdit.getAuthorIntention())
                .build();
    }

    @Transactional
    public void editQuestionsOfPost(Long postId, List<QuestionEdit> questionEdits, Long memberId) {
        Post post = findPost(postId);
        validatePostOwner(post, memberId);
        List<Question> questionsInPost = post.getQuestions(); 멤버입장에서 오더스를 알 필요가 없어요 query가 오더에서 시작되는게 맞아보여요

        for (QuestionEdit questionEdit : questionEdits) {
            Question question = questionsInPost.stream()
                    .filter(questionInPost -> questionInPost.getId().equals(questionEdit.getQuestionId()))
                    .findFirst()
                    .orElseThrow(QuestionNotFound::new);

            QuestionEditor questionEditor = createEditor(questionEdit, question);
            question.edit(questionEditor);
            questionRepository.save(question);
        }
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFound::new);
    }

    private void validatePostOwner(Post post, Long memberId) {
        if (!post.isOwnedBy(memberId)) {
            throw new InvalidRequest("memberId", "소유자가 아닙니다");
        }
    }
}
