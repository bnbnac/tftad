package com.tftad.service;

import com.tftad.TestUtility;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Member;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.request.MemberEdit;
import com.tftad.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestUtility testUtility;

    private Member member;

    @BeforeEach
    void clean() {
        memberRepository.deleteAll();
        channelRepository.deleteAll();
        postRepository.deleteAll();
        member = testUtility.createMember();
    }

    @Test
    @DisplayName("멤버 조회")
    void test1() {
        Long memberId = memberRepository.save(member).getId();

        // when
        MemberResponse memberResponse = memberService.get(memberId);

        // then
        assertThat(memberResponse.getEmail()).isEqualTo("email");
        assertThat(memberResponse.getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("멤버 조회 - 존재하지 않는 멤버")
    void test2() {
        Long memberId = memberRepository.save(member).getId();

        // when then
        assertThatThrownBy(() -> memberService.get(memberId + 1)).isInstanceOf(MemberNotFound.class);
    }

    @Test
    @DisplayName("멤버 수정")
    void test3() {
        Long memberId = memberRepository.save(member).getId();
        MemberEdit memberEdit = MemberEdit.builder()
                .password("newPassword")
                .name(null)
                .build();
        AuthenticatedMember authenticatedMember = AuthenticatedMember.builder()
                .id(memberId)
                .build();

        // when
        memberService.edit(memberId, memberEdit, authenticatedMember);

        // then
        Member editedMember = memberRepository.findById(memberId).orElseThrow(MemberNotFound::new);

        assertThat(passwordEncoder.matches("newPassword", editedMember.getPassword())).isTrue();
        assertThat(editedMember.getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("멤버 수정 - 존재하지 않는 멤버")
    void test4() {
        Long memberId = memberRepository.save(member).getId();
        MemberEdit memberEdit = MemberEdit.builder()
                .password("newPassword")
                .name("newName")
                .build();
        AuthenticatedMember authenticatedMember = AuthenticatedMember.builder()
                .id(memberId)
                .build();

        // when
        memberService.edit(memberId, memberEdit, authenticatedMember);

        // when then
        assertThatThrownBy(() -> {
            memberService.edit(memberId + 1, memberEdit, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("멤버 수정 - 본인이 아닌 멤버")
    void test5() {
        Long memberId = memberRepository.save(member).getId();
        MemberEdit memberEdit = MemberEdit.builder()
                .password("newPassword")
                .name("newName")
                .build();
        AuthenticatedMember authenticatedMember = AuthenticatedMember.builder()
                .id(memberId)
                .build();

        // when
        memberService.edit(memberId, memberEdit, authenticatedMember);

        // when then
        assertThatThrownBy(() -> {
            memberService.edit(memberId + 1, memberEdit, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }
}
