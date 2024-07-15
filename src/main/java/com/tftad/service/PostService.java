package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.*;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.ExtractorResultError;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.PostRepository;
import com.tftad.request.ExtractorCompletion;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import com.tftad.response.PostResponseDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final AuthService authService;
    private final PostRepository postRepository;
    private final ChannelRepository channelRepository;
    private final OAuthService oAuthService;
    private final QuestionByLifecycleOfPostService questionByLifecycleOfPostService;

    public Long write(PostCreateDto postCreateDto, AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);

        validatePostedVideo(postCreateDto.getVideoId());
        String youtubeChannelId = oAuthService.getYoutubeChannelId(postCreateDto.getVideoId());
        Channel channel = findChannel(youtubeChannelId);
        validateChannelOwner(member.getId(), channel);

        Post post = createPost(postCreateDto, channel.getId(), member.getId());
        return postRepository.save(post).getId();
    }

    private void validatePostedVideo(String videoId) {
        postRepository.findByVideoId(videoId).ifPresent(p -> {
            throw new InvalidRequest("videoId", "이미 등록된 영상입니다");
        });
    }

    private Channel findChannel(String youtubeChannelId) {
        return channelRepository.findByYoutubeChannelId(youtubeChannelId).orElseThrow(ChannelNotFound::new);
    }

    private void validateChannelOwner(Long memberId, Channel channel) {
        if (!channel.isOwnedBy(memberId)) {
            throw new InvalidRequest("channel", "소유자가 아닙니다");
        }
    }

    private Post createPost(PostCreateDto postCreateDto, Long channelId, Long memberId) {
        return Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .videoId(postCreateDto.getVideoId())
                .memberId(memberId)
                .channelId(channelId)
                .build();
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFound::new);
    }

    @Transactional
    public PostResponseDetail getPostDetails(Long postId) {
        return postRepository.getPostWithDetails(postId).orElseThrow(PostNotFound::new);
    }

    public PostResponse getSimple(Long postId) {
        return new PostResponse(findPost(postId));
    }

    @Transactional
    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostResponse> getListOf(Long memberId, PostSearch postSearch) {
        return postRepository.getListOfMember(memberId, postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(Long postId, PostEdit postEdit, AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);
        editPost(postId, postEdit, member.getId());

        questionByLifecycleOfPostService.editQuestionsOfPost(postId, postEdit);
    }

    private void editPost(Long postId, PostEdit postEdit, Long memberId) {
        Post post = findPost(postId);
        validatePostOwner(memberId, post);
        PostEditor postEditor = createEditor(postEdit, post);
        post.edit(postEditor);
    }

    private void validatePostOwner(Long memberId, Post post) {
        if (!post.isOwnedBy(memberId)) {
            throw new InvalidRequest("memberId", "소유자가 아닙니다");
        }
    }

    private PostEditor createEditor(PostEdit postEdit, Post post) {
        return post.toEditorBuilder()
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();
    }

    @Transactional
    public void delete(Long postId, AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);
        Post post = findPost(postId);
        validatePostOwner(member.getId(), post);

        questionByLifecycleOfPostService.deleteQuestionsOfPost(postId);
        postRepository.delete(post);
    }

    @Transactional
    public void processExtractorCompletion(ExtractorCompletion extractorCompletion) {
        Post post = findPost(extractorCompletion.getPostId());
        validatePublishedPost(post);
        validateExtractorResult(post, extractorCompletion.getResult());

        questionByLifecycleOfPostService.createQuestionsOfPost(post, extractorCompletion.getResult());
        post.publish();
    }

    private void validatePublishedPost(Post post) {
        if (post.getPublished()) {
            throw new InvalidRequest("postId", "이미 발행된 게시글입니다");
        }
    }

    private void validateExtractorResult(Post post, List<String> extractorResult) {
        if (extractorResult.isEmpty() || extractorResult.size() % 2 == 1) {
            postRepository.delete(post);
            throw new ExtractorResultError(post.getMemberId(), post.getId());
        }
    }

    @Transactional
    public boolean isPublishedPostOwner(Long postId, AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        validatePostOwner(member.getId(), post);

        return post.getPublished();
    }
}
