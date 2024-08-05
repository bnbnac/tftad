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

    private final MemberService memberService;
    private final QuestionRepository questionRepository;

    @Transactional
    public void editQuestion(QuestionEdit questionEdit, Long postId) {
        Question question = findQuestion(questionEdit.getQuestionId());
        validateQuestionInPost(question, postId);
        QuestionEditor questionEditor = createEditor(questionEdit, question);
        question.edit(questionEditor);
    }

    private Question findQuestion(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);
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

    @Transactional
    public QuestionDeleteDto delete(Long questionId, AuthenticatedMember authenticatedMember) {
        Member member = memberService.findMember(authenticatedMember);
        Question question = findQuestion(questionId);
        validatePostOwner(question.getPost(), member.getId());

        questionRepository.delete(question);

        return QuestionDeleteDto.builder()
                .filename(question.generateFilename())
                .postId(question.getPost().getId())
                .build();
    }

    private void validatePostOwner(Post post, Long memberId) {
        if (!post.isOwnedBy(memberId)) {
            throw new InvalidRequest("memberId", "소유자가 아닙니다");
        }
    }

    @Transactional
    public List<QuestionResponse> getListOf(Long postId) {
        return questionRepository.findByPostIdOrderByStartTimeAsc(postId)
                .stream()
                .map(QuestionResponse::new)
                .collect(Collectors.toList());
    }
}
