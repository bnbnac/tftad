package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}