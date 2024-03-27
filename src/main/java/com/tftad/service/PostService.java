package com.tftad.service;

import com.tftad.domain.*;
import com.tftad.exception.ExtractorServerError;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.PostRepository;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import com.tftad.response.PostResponseDetail;
import io.jsonwebtoken.lang.Assert;
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
    private final MemberService memberService;
    private final ChannelService channelService;

    @Transactional
    public Long savePost(PostCreateDto postCreateDto, Long channelId) {
        Member member = memberService.getMemberById(postCreateDto.getMemberId());
        Channel channel = channelService.getChannelById(channelId);

        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .videoId(postCreateDto.getVideoId())
                .member(member)
                .channel(channel)
                .build();
        return postRepository.save(post).getId();
    }

    @Transactional
    public PostResponseDetail get(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        return new PostResponseDetail(post);
    }

    @Transactional
    public PostResponse getSimple(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        return new PostResponse(post);
    }

    @Transactional
    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostResponse> getPostListOfMember(Long memberId, PostSearch postSearch) {
        return postRepository.getListOfMember(memberId, postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void edit(PostEditDto postEditDto) {
        Post post = postRepository.findById(postEditDto.getPostId()).orElseThrow(PostNotFound::new);
        if (!postEditDto.getMemberId().equals(post.getMember().getId())) {
            throw new InvalidRequest("memberId", "소유자만 게시글을 수정할 수 있습니다");
        }

        PostEditor postEditor = post.toEditorBuilder()
                .title(postEditDto.getTitle())
                .content(postEditDto.getContent())
                .build();
        post.edit(postEditor);
        postRepository.save(post);
    }

    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        if (!memberId.equals(post.getMember().getId())) {
            throw new InvalidRequest("postId", "게시글의 작성자만 글을 삭제할 수 있습니다");
        }
        postRepository.delete(post);
    }

    public void validatePublishedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new InvalidRequest("postId", "존재하지 않는 게시글입니다"));
        if (post.getPublished()) {
            throw new InvalidRequest("postId", "이미 발행된 게시글입니다");
        }
    }

    public void showPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        post.show();
        postRepository.save(post);
    }

    @Transactional
    public void validatePostedVideo(PostCreateDto postCreateDto) {
        postRepository.findByVideoId(postCreateDto.getVideoId())
                .ifPresent(p -> {
                    throw new InvalidRequest(
                            "videoId", "이미 등록된 영상입니다. postId: " + p.getId()
                    );
                });
    }

    public void validateToGetPosition(Long memberId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        if (!post.getMember().getId().equals(memberId)) {
            throw new InvalidRequest("postId", "게시글의 작성자만 작업상황을 조회할 수 있습니다");
        }
    }

    public void validateExtractorResultOrDeletePost(Long postId, List<String> extractorResult) {
        Assert.notNull(postId, "post id must not be null");
        Assert.notNull(extractorResult, "extractor result must not be null");

        if (extractorResult.isEmpty() || extractorResult.size() % 2 == 1) {
            postRepository.deleteById(postId);
            throw new ExtractorServerError();
        }
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFound::new);
    }
}
