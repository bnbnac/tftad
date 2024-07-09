package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.*;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.QuestionNotFound;
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

    private final AuthService authService;
    private final QuestionRepository questionRepository;

    @Transactional
    public void saveQuestionsFromExtractorResult(Post post, List<String> extractorResult) {
        for (int i = 0; i < extractorResult.size(); i += 2) {
            Question question = createQuestion(post, extractorResult.get(i), extractorResult.get(i + 1));
            questionRepository.save(question);
        }
    }

    private Question createQuestion(Post post, String startTime, String endTime) {
        return Question.builder()
                .startTime(startTime)
                .endTime(endTime)
                .post(post)
                .build();
    }

    @Transactional
    public QuestionDeleteDto delete(Long questionId, AuthenticatedMember authenticatedMember) {
        Member member = authService.checkMember(authenticatedMember);
        Question question = findQuestion(questionId);
        validatePostOwner(question.getPost(), member.getId());

        questionRepository.delete(question);

        return QuestionDeleteDto.builder()
                .filename(question.generateFilename())
                .postId(question.getPost().getId())
                .build();
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);
    }

    @Transactional
    public List<QuestionResponse> getListOf(Long postId) {
        return questionRepository.findByPostIdOrderByStartTimeAsc(postId)
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }

    public void editQuestions(Post post, List<QuestionEdit> questionEdits, AuthenticatedMember authenticatedMember) {
        Member member = authService.checkMember(authenticatedMember);
        validatePostOwner(post, member.getId());

        for (QuestionEdit questionEdit : questionEdits) {
            editQuestion(questionEdit, post.getId());
        }
    }

    private void validatePostOwner(Post post, Long memberId) {
        if (!post.isOwnedBy(memberId)) {
            throw new InvalidRequest("memberId", "소유자가 아닙니다");
        }
    }

    private void editQuestion(QuestionEdit questionEdit, Long postId) {
        Question question = findQuestion(questionEdit.getQuestionId());
        validateQuestionInPost(question, postId);
        QuestionEditor questionEditor = createEditor(questionEdit, question);
        question.edit(questionEditor);
        questionRepository.save(question);
    }

    private void validateQuestionInPost(Question question, Long postId) {
        if (!postId.equals(question.getPost().getId())) {
            throw new InvalidRequest("questionId", "invalid questionId of the post");
        }
    }

    private QuestionEditor createEditor(QuestionEdit questionEdit, Question question) {
        return question.toEditorBuilder()
                .authorIntention(questionEdit.getAuthorIntention())
                .build();
    }

    public void deleteQuestionsOfPost(Post post, Member member, AuthenticatedMember authenticatedMember) {
        authService

        List<Question> questions = questionRepository.findByPostId(post.getId());
        questions.forEach(question -> delete(question.getId(), authenticatedMember)); 위에 edit도 auth를 이런식으로 넘긴다?
                아니? delete 메서드 자체를 auth 아랫부분 자르면 될까?
    }
}
