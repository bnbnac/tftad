package com.tftad.service;

import com.tftad.TestUtility;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.PostCreateDto;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import com.tftad.request.PostEdit;
import com.tftad.request.QuestionEdit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class PostServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestUtility testUtility;

    @MockBean
    private OAuthService oAuthService;

    @MockBean
    private AuthService authService;

    @Autowired
    private PostService postService;

    @MockBean
    private QuestionByLifecycleOfPostService questionByLifecycleOfPostService;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private QuestionRepository questionRepository;

    private Member member;
    private Channel channel;
    private AuthenticatedMember authenticatedMember;
    private PostCreateDto postCreateDto;
    private Long setupPostId;

    @BeforeEach
    void setup() {
        questionRepository.deleteAll();
        postRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();

        member = testUtility.createMember();
        memberRepository.save(member);

        channel = testUtility.createChannel(member);
        channelRepository.save(channel);

        Post setupPost = testUtility.createPost(member, channel);
        setupPostId = postRepository.save(setupPost).getId();

        authenticatedMember = AuthenticatedMember.builder()
                .id(member.getId())
                .build();
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void test1() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);
        when(oAuthService.getYoutubeChannelId("videoIdNew")).thenReturn("youtubeChannelId");

        postCreateDto = PostCreateDto.builder()
                .title("title")
                .content("content")
                .videoId("videoIdNew")
                .build();

        // when
        postService.write(postCreateDto, authenticatedMember);

        // then
        Post post = postRepository.findById(setupPostId + 1L).get();
        assertThat(post.getMemberId()).isEqualTo(member.getId());
        assertThat(post.getChannelId()).isEqualTo(channel.getId());
        assertThat(post.getTitle()).isEqualTo("title");
        assertThat(post.getVideoId()).isEqualTo("videoIdNew");
        assertThat(post.getPublished()).isFalse();
        assertThat(post.getContent()).isEqualTo("content");
    }

    @Test
    @DisplayName("작성 실패 - 인증되지 않은 멤버")
    void test2() {
        when(authService.check(any(AuthenticatedMember.class))).thenThrow(MemberNotFound.class);
        when(oAuthService.getYoutubeChannelId("videoIdNew")).thenReturn("youtubeChannelId");

        postCreateDto = PostCreateDto.builder()
                .title("title")
                .content("content")
                .videoId("videoIdNew")
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.write(postCreateDto, authenticatedMember);
        }).isInstanceOf(MemberNotFound.class);
    }

    @Test
    @DisplayName("작성 실패 - 동일 비디오로 작성된 게시물이 존재하는 경우")
    void test3() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);
        when(oAuthService.getYoutubeChannelId("videoIdNew")).thenReturn("youtubeChannelId");

        postCreateDto = PostCreateDto.builder()
                .title("title")
                .content("content")
                .videoId("videoIdNew")
                .build();

        postService.write(postCreateDto, authenticatedMember);

        // when then
        assertThatThrownBy(() -> {
            postService.write(postCreateDto, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("작성 실패 - youtubeVideoUrl로 OAuth youtubeChannelId 조회에 실패한 경우")
    void test4() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);
        when(oAuthService.getYoutubeChannelId("videoIdNew")).thenThrow(InvalidRequest.class);

        postCreateDto = PostCreateDto.builder()
                .title("title")
                .content("content")
                .videoId("videoIdNew")
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.write(postCreateDto, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("작성 실패 - OAuth로 조회된 채널이 본 서비스에 등록되지 않은 경우")
    void test5() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);
        when(oAuthService.getYoutubeChannelId("videoIdNew")).thenReturn("youtubeChannelIdOther");

        postCreateDto = PostCreateDto.builder()
                .title("title")
                .content("content")
                .videoId("videoIdNew")
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.write(postCreateDto, authenticatedMember);
        }).isInstanceOf(ChannelNotFound.class);
    }

    @Test
    @DisplayName("작성 실패 - 채널의 소유자가 아닌 경우")
    void test6() {
        Member otherMember = testUtility.createMember();
        memberRepository.save(otherMember);

        when(authService.check(any(AuthenticatedMember.class))).thenReturn(otherMember);
        when(oAuthService.getYoutubeChannelId("videoIdNew")).thenReturn("youtubeChannelId");

        postCreateDto = PostCreateDto.builder()
                .title("title")
                .content("content")
                .videoId("videoIdNew")
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.write(postCreateDto, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void test7() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        QuestionEdit questionEdit = QuestionEdit.builder()
                .questionId(1L)
                .authorIntention("authorIntentionUpdate")
                .build();

        PostEdit postEdit = PostEdit.builder()
                .title("titleUpdate")
                .content("contentUpdate")
                .questionEdits(List.of(questionEdit))
                .build();

        // when
        postService.edit(setupPostId, postEdit, authenticatedMember);

        // then
        Post post = postRepository.findById(setupPostId).get();
        assertThat(post.getTitle()).isEqualTo("titleUpdate");
        assertThat(post.getContent()).isEqualTo("contentUpdate");
    }

    @Test
    @DisplayName("수정 실패 - 인증되지 않은 멤버")
    void test8() {
        when(authService.check(any(AuthenticatedMember.class))).thenThrow(MemberNotFound.class);

        QuestionEdit questionEdit = QuestionEdit.builder()
                .questionId(1L)
                .authorIntention("authorIntentionUpdate")
                .build();

        PostEdit postEdit = PostEdit.builder()
                .title("titleUpdate")
                .content("contentUpdate")
                .questionEdits(List.of(questionEdit))
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.edit(setupPostId, postEdit, authenticatedMember);
        }).isInstanceOf(MemberNotFound.class);
    }

    @Test
    @DisplayName("수정 실패 - 존재하지 않는 게시물")
    void test9() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        QuestionEdit questionEdit = QuestionEdit.builder()
                .questionId(1L)
                .authorIntention("authorIntentionUpdate")
                .build();

        PostEdit postEdit = PostEdit.builder()
                .title("titleUpdate")
                .content("contentUpdate")
                .questionEdits(List.of(questionEdit))
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.edit(setupPostId + 100L, postEdit, authenticatedMember);
        }).isInstanceOf(PostNotFound.class);
    }


    @Test
    @DisplayName("수정 실패 - 소유자가 아닌 멤버")
    void test10() {
        Member otherMember = testUtility.createMember();
        memberRepository.save(otherMember);

        when(authService.check(any(AuthenticatedMember.class))).thenReturn(otherMember);

        QuestionEdit questionEdit = QuestionEdit.builder()
                .questionId(1L)
                .authorIntention("authorIntentionUpdate")
                .build();

        PostEdit postEdit = PostEdit.builder()
                .title("titleUpdate")
                .content("contentUpdate")
                .questionEdits(List.of(questionEdit))
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.edit(setupPostId, postEdit, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("수정 실패 - question 수정중 exception 발생")
    void test11() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);
        doThrow(InvalidRequest.class).when(questionByLifecycleOfPostService)
                .editQuestionsOfPost(anyLong(), any(PostEdit.class));

        QuestionEdit questionEdit = QuestionEdit.builder()
                .questionId(1L)
                .authorIntention("authorIntentionUpdate")
                .build();

        PostEdit postEdit = PostEdit.builder()
                .title("titleUpdate")
                .content("contentUpdate")
                .questionEdits(List.of(questionEdit))
                .build();

        // when then
        assertThatThrownBy(() -> {
            postService.edit(setupPostId, postEdit, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void test12() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        // when
        postService.delete(setupPostId, authenticatedMember);

        // then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("삭제 실패 - 인증되지 않은 멤버")
    void test13() {
        when(authService.check(any(AuthenticatedMember.class))).thenThrow(MemberNotFound.class);

        // when then
        assertThatThrownBy(() -> {
            postService.delete(setupPostId, authenticatedMember);
        }).isInstanceOf(MemberNotFound.class);
    }

    @Test
    @DisplayName("삭제 실패 - 존재하지 않는 게시물")
    void test14() {
        when(authService.check(any(AuthenticatedMember.class))).thenReturn(member);

        // when then
        assertThatThrownBy(() -> {
            postService.delete(setupPostId + 100L, authenticatedMember);
        }).isInstanceOf(PostNotFound.class);
    }

    @Test
    @DisplayName("삭제 실패 - 소유자가 아닌 멤버")
    void test15() {
        Member otherMember = testUtility.createMember();
        memberRepository.save(otherMember);

        when(authService.check(any(AuthenticatedMember.class))).thenReturn(otherMember);

        // when then
        assertThatThrownBy(() -> {
            postService.delete(setupPostId, authenticatedMember);
        }).isInstanceOf(InvalidRequest.class);
    }
}