package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.domain.QuestionEditDto;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.QuestionNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import com.tftad.response.QuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class QuestionServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void clean() {
        questionRepository.deleteAll();
        postRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("questions 저장")
    void test1() {
        Member member = Member.builder()
                .name("name")
                .email("email")
                .password("pswd")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("title")
                .videoId("vid")
                .member(member)
                .content("content")
                .build();
        Long postId = postRepository.save(post).getId();

        List<String> extractorResult = Arrays.asList("start1", "end1", "start2", "end2");

        // when
        questionService.saveQuestionsFromExtractorResult(postId, extractorResult);

        Iterator<Question> iterator = questionRepository.findAll().iterator();
        Question q1 = iterator.next();
        Question q2 = iterator.next();

        // then
        assertEquals(2L, questionRepository.count());
        assertEquals("start1", q1.getStartTime());
        assertEquals("end1", q1.getEndTime());
        assertEquals("start2", q2.getStartTime());
        assertEquals("end2", q2.getEndTime());
    }

    @Test
    @DisplayName("question 1개 조회")
    void test2_1() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .endTime("111112")
                .startTime("010000")
                .authorIntention("hello1")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        // when
        QuestionResponse questionResponse = questionService.get(questionId);

        // then
        assertEquals(11 * 3600 + 11 * 60 + 12, questionResponse.getEndTimeOnSecond());
        assertEquals("hello1", questionResponse.getAuthorIntention());
        assertEquals(questionId, questionResponse.getId());
        assertEquals("010000_111112.mp4", questionResponse.getFileName());
    }

    @Test
    @DisplayName("question 1개 조회 - 존재하지 않는 question")
    void test2_2() {

        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .endTime("111112")
                .startTime("010000")
                .authorIntention("hello1")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        // when then
        assertThrows(QuestionNotFound.class, () -> questionService.get(questionId + 1));
    }

    @Test
    @DisplayName("question 삭제")
    void test3() {
        Member member1 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member1).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member1)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .endTime("111112")
                .startTime("010000")
                .authorIntention("hello1")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        // when
        questionService.delete(memberId, questionId);

        // then
        assertEquals(0, questionRepository.count());
    }

    @Test
    @DisplayName("question 삭제 - 존재하지 않는 question")
    void test4() {
        Member member1 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member1).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member1)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .endTime("111112")
                .startTime("010000")
                .authorIntention("hello1")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        // when then
        assertThrows(QuestionNotFound.class, () -> {
            questionService.delete(memberId, questionId + 1L);
        });
    }
    @Test
    @DisplayName("question 삭제 - 작성자가 아닌 멤버")
    void test5() {
        Member member1 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId1 = memberRepository.save(member1).getId();

        Member member2 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId2 = memberRepository.save(member2).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member1)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .endTime("111112")
                .startTime("010000")
                .authorIntention("hello1")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        // when then
        assertThrows(InvalidRequest.class, () -> {
            questionService.delete(memberId2, questionId);
        });
    }

    @Test
    @DisplayName("question 수정 - 존재하지 않는 question")
    void test6() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .authorIntention("hello")
                .endTime("000100")
                .startTime("000001")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        // when
        QuestionEditDto questionEditDto = QuestionEditDto.builder()
                .questionId(questionId + 1).memberId(memberId).authorIntention("hi").build();

        // then
        assertThrows(QuestionNotFound.class, () -> {
            questionService.edit(questionEditDto);
        });
    }

    @Test
    @DisplayName("question 수정 - 작성자가 아닌 멤버")
    void test7() {
        Member member1 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId1 = memberRepository.save(member1).getId();

        Member member2 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId2 = memberRepository.save(member2).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member1)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .authorIntention("hello")
                .endTime("000100")
                .startTime("000001")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        // when
        QuestionEditDto questionEditDto = QuestionEditDto.builder()
                .questionId(questionId)
                .authorIntention("modified hello")
                .memberId(memberId2)
                .build();

        // then
        assertThrows(InvalidRequest.class, () -> {
            questionService.edit(questionEditDto);
        });
    }

    @Test
    @DisplayName("question 수정")
    void test8() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        Question question = Question.builder()
                .authorIntention("hello")
                .endTime("000100")
                .startTime("000001")
                .post(post)
                .build();
        Long questionId = questionRepository.save(question).getId();

        QuestionEditDto questionEditDto = QuestionEditDto.builder()
                .questionId(questionId)
                .authorIntention("modified hello")
                .memberId(memberId)
                .build();

        // when
        questionService.edit(questionEditDto);
        Question changedQuestion = questionRepository.findById(questionId).orElseThrow(QuestionNotFound::new);

        // then
        assertEquals("modified hello", changedQuestion.getAuthorIntention());
        assertEquals("000001", changedQuestion.getStartTime());
        assertEquals("000100", changedQuestion.getEndTime());
    }
}