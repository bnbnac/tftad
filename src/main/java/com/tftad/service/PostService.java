package com.tftad.service;

import com.tftad.domain.*;
import com.tftad.exception.*;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.PostRepository;
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

    private final PostRepository postRepository;
    private final ChannelRepository channelRepository;
    private final QuestionService questionService;
    private final OAuthService oAuthService;

    @Transactional
    public Long write(PostCreateDto postCreateDto) {
        validatePostedVideo(postCreateDto.getVideoId());
        String youtubeChannelId = oAuthService.getYoutubeChannelId(postCreateDto.getVideoId());
        Channel channel = findChannel(youtubeChannelId);
        validateChannelOwner(postCreateDto.getMemberId(), channel);

        Post post = createPost(postCreateDto, channel);
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
            throw new InvalidRequest("channel", "채널의 소유자가 아닙니다");
        }
    }

    private Post createPost(PostCreateDto postCreateDto, Channel channel) {
        return Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .videoId(postCreateDto.getVideoId())
                .member(channel.getMember())
                .channel(channel)
                .build();
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFound::new);
    }

    @Transactional
    public PostResponseDetail get(Long postId) {
        Post post = findPost(postId);
        return new PostResponseDetail(post);
    }

    @Transactional
    public PostResponse getSimple(Long postId) {
        Post post = findPost(postId);
        return new PostResponse(post);
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
    public void edit(PostEditDto postEditDto) {
        Post post = findPost(postEditDto.getPostId());
        validatePostOwner(postEditDto.getMemberId(), post);
        PostEditor postEditor = createEditor(postEditDto, post);

        post.edit(postEditor);
        postRepository.save(post);
    }

    public void validatePostOwner(Long memberId, Post post) {
        if (!post.isOwnedBy(memberId)) {
            throw new InvalidRequest("memberId", "게시글 작성자가 아닙니다");
        }
    }

    private PostEditor createEditor(PostEditDto postEditDto, Post post) {
        return post.toEditorBuilder()
                .title(postEditDto.getTitle())
                .content(postEditDto.getContent())
                .build();
    }

    // cascade questions
    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = findPost(postId);
        validatePostOwner(memberId, post);
        postRepository.delete(post);
    }

    @Transactional
    public void processExtractorCompletion(Long postId, List<String> extractorResult) {
        Post post = findPost(postId);
        validatePublishedPost(post);
        validateExtractorResult(postId, extractorResult);

        questionService.saveQuestionsFromExtractorResult(postId, extractorResult);
        post.show();
        postRepository.save(post);
    }

    private void validatePublishedPost(Post post) {
        if (post.getPublished()) {
            throw new InvalidRequest("postId", "이미 발행된 게시글입니다");
        }
    }

    private void validateExtractorResult(Long postId, List<String> extractorResult) {
        if (extractorResult.isEmpty() || extractorResult.size() % 2 == 1) {
            postRepository.deleteById(postId);
            throw new ExtractorResultError();
        }
    }
}
