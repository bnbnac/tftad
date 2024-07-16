package com.tftad.service;

import com.tftad.TestUtility;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.ChannelCreateDto;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ChannelServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtility testUtility;

    @MockBean
    private OAuthService oAuthService;

    @MockBean
    private AuthService authService;

    @MockBean
    private ChannelInheritService channelInheritService;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private QuestionRepository questionRepository;

    private Member member;
    private Member deletedMember;
    private Channel setupChannel;
    private Post setupPost;
    private Channel deletedChannel;
    private AuthenticatedMember authenticatedMember;
    private ChannelCreateDto channelCreateDto;
    private Long setupChannelId;
    private Long setupPostId;
    private Long deletedMemberId;

    @BeforeEach
    void setup() {
        questionRepository.deleteAll();
        postRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();

        member = testUtility.createMember();
        memberRepository.save(member);

        deletedMember = testUtility.createMember();
        deletedMemberId = memberRepository.save(deletedMember).getId();

        setupChannel = testUtility.createChannel(member);
        setupChannelId = channelRepository.save(setupChannel).getId();

        deletedChannel = testUtility.createDeletedChannel();
        channelRepository.save(deletedChannel);

        setupPost = testUtility.createPost(member, setupChannel);
        setupPostId = postRepository.save(setupPost).getId();

        authenticatedMember = AuthenticatedMember.builder()
                .id(member.getId())
                .build();
    }

    @Test
    @DisplayName("신규채널 등록 성공")
    void test1() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        channelCreateDto = ChannelCreateDto.builder()
                .youtubeChannelId("youtubeChannelIdNew")
                .thumbnail("thumbnail")
                .channelTitle("channelTitle")
                .build();

        // when
        channelService.addChannel(channelCreateDto, authenticatedMember);

        // then
        assertThat(channelRepository.count()).isEqualTo(3L);
        Iterator<Channel> iterator = channelRepository.findAll().iterator();
        iterator.next();
        iterator.next();
        Channel channel = iterator.next();
        assertThat(channel.getYoutubeChannelId()).isEqualTo("youtubeChannelIdNew");
        assertThat(channel.getChannelTitle()).isEqualTo("channelTitle");
        assertThat(channel.getThumbnail()).isEqualTo("thumbnail");
    }

    @Test
    @DisplayName("채널 등록 실패 - 인증되지 않은 멤버")
    void test2() {
        when(authService.check(any(AuthenticatedMember.class))).thenThrow(MemberNotFound.class);

        channelCreateDto = ChannelCreateDto.builder()
                .youtubeChannelId("youtubeChannelIdNew")
                .thumbnail("thumbnail")
                .channelTitle("channelTitle")
                .build();

        // when then
        assertThatThrownBy(() -> {
            channelService.addChannel(channelCreateDto, authenticatedMember);
        }).isInstanceOf(MemberNotFound.class);
    }

    @Test
    @DisplayName("기존채널 등록 성공")
    void test3() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        channelCreateDto = ChannelCreateDto.builder()
                .youtubeChannelId("youtubeChannelIdDeleted")
                .thumbnail("thumbnail")
                .channelTitle("channelTitle")
                .build();

        // when
        channelService.addChannel(channelCreateDto, authenticatedMember);

        // then
        Channel channel = channelRepository.findById(setupChannelId + 1L).get();
        assertThat(channel.getYoutubeChannelId()).isEqualTo("youtubeChannelIdDeleted");
        assertThat(channel.getChannelTitle()).isEqualTo("channelTitle");
        assertThat(channel.getThumbnail()).isEqualTo("thumbnail");
    }

    @Test
    @DisplayName("기존채널 등록 실패 - 다른 멤버가 소유한 채널")
    void test4() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        channelCreateDto = ChannelCreateDto.builder()
                .youtubeChannelId("youtubeChannelId")
                .thumbnail("thumbnail")
                .channelTitle("channelTitle")
                .build();

        // when then
        assertThatThrownBy(() -> {
            channelService.addChannel(channelCreateDto, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("기존채널 등록 실패 - 다른 멤버가 소유한 채널")
    void test5() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        channelCreateDto = ChannelCreateDto.builder()
                .youtubeChannelId("youtubeChannelId")
                .thumbnail("thumbnail")
                .channelTitle("channelTitle")
                .build();

        // when then
        assertThatThrownBy(() -> {
            channelService.addChannel(channelCreateDto, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("채널 삭제 성공 - DELETED_MEMBER에 인계")
    void test6() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);
        when(memberService.getDeletedMember()).thenReturn(deletedMember);

        // when
        channelService.delete(setupChannelId, authenticatedMember);

        // then
        Channel channel = channelRepository.findById(setupChannelId).get();
        assertThat(channel.getMemberId()).isEqualTo(deletedMemberId);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 인증되지 않은 멤버")
    void test7() {
        when(authService.check(any(AuthenticatedMember.class))).thenThrow(MemberNotFound.class);

        // when then
        assertThatThrownBy(() -> {
            channelService.delete(setupChannelId, authenticatedMember);
        }).isInstanceOf(MemberNotFound.class);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
    void test8() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        // when then
        assertThatThrownBy(() -> {
            channelService.delete(setupChannelId + 100L, authenticatedMember);
        }).isInstanceOf(ChannelNotFound.class);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 소유자가 아닌 멤버")
    void test9() {
        Member otherMember = testUtility.createMember();
        memberRepository.save(otherMember);

        when(authService.check(any(AuthenticatedMember.class))).thenReturn(otherMember);

        // when then
        assertThatThrownBy(() -> {
            channelService.delete(setupChannelId, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }
}