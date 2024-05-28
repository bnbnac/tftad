package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.domain.MemberEditDto;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import com.tftad.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @BeforeEach
    void clean() {
        questionRepository.deleteAll();
        postRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("멤버 조회")
    void test1() {
        Member member = Member.builder()
                .email("email")
                .name("name")
                .password("paswd")
                .build();
        Long memberId = memberRepository.save(member).getId();

        // when
        MemberResponse memberResponse = memberService.get(memberId);

        // then
        assertEquals("email", memberResponse.getEmail());
        assertEquals("name", memberResponse.getName());
    }

    @Test
    @DisplayName("멤버 조회 - 존재하지 않는 멤버")
    void test2() {
        Member member = Member.builder()
                .email("email")
                .name("name")
                .password("paswd")
                .build();
        Long memberId = memberRepository.save(member).getId();

        // when then
        assertThrows(MemberNotFound.class, () -> memberService.get(memberId + 1));
    }

    @Test
    @DisplayName("멤버 수정")
    void test3() {
        Member member = Member.builder()
                .email("email")
                .name("name")
                .password("pswd")
                .build();
        Long memberId = memberRepository.save(member).getId();

        MemberEditDto memberEditDto = MemberEditDto.builder()
                .name("changedName")
                .memberId(memberId)
                .build();

        // when
        memberService.edit(memberEditDto);
        Member changedMember = memberRepository.findById(memberId).orElseThrow(MemberNotFound::new);

        // then
        assertEquals("changedName", changedMember.getName());
        assertEquals(memberId, changedMember.getId());
        assertNotNull(changedMember.getPassword());
    }

    @Test
    @DisplayName("멤버 수정 - 존재하지 않는 멤버")
    void test4() {
        Member member = Member.builder()
                .email("email")
                .name("name")
                .password("paswd")
                .build();
        Long memberId = memberRepository.save(member).getId();

        // when then
        assertThrows(MemberNotFound.class, () -> memberService.get(memberId + 1));
    }
}
