package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.PostCreateDto;
import com.tftad.domain.PostEditDto;
import com.tftad.exception.ExtractorServerError;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import com.tftad.response.PostResponseDetail;
import com.tftad.service.ChannelService;
import com.tftad.service.ExtractorService;
import com.tftad.service.OAuthService;
import com.tftad.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tftad.utility.Utility.extractVideoId;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final OAuthService oAuthService;
    private final ChannelService channelService;
    private final ExtractorService extractorService;

    @PostMapping("/posts")
    public Long post(AuthenticatedMember authenticatedMember, @RequestBody @Valid PostCreate postCreate) {
        PostCreateDto postCreateDto = createPostCreateDto(authenticatedMember, postCreate);
        String youtubeChannelId = oAuthService.queryVideoResourceToGetChannelId(postCreateDto.getVideoId());
        Long channelId = channelService.validateChannelOwner(postCreateDto.getMemberId(), youtubeChannelId);

        postService.validatePostedVideo(postCreateDto);
        Long postId = postService.savePost(postCreateDto, channelId);

        queryToExtractor(postCreateDto.getVideoId(), postCreateDto.getMemberId(), postId);
        return postId;
    }

    private PostCreateDto createPostCreateDto(AuthenticatedMember authenticatedMember, PostCreate postCreate) {
        String videoId = extractVideoId(postCreate.getVideoUrl());

        return postCreate.toPostCreateDtoBuilder()
                .memberId(authenticatedMember.getId())
                .videoId(videoId)
                .build();
    }

    private void queryToExtractor(String videoId, Long memberId, Long postId) {
        try {
            extractorService.queryAnalysis(videoId, memberId, postId);
        } catch (Exception e) {
            postService.delete(memberId, postId);
            throw new ExtractorServerError();
        }
    }

    @GetMapping("/posts/{postId}")
    public PostResponseDetail get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts/simple/{postId}")
    public PostResponse getSimple(@PathVariable Long postId) {
        return postService.getSimple(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    @GetMapping("/posts/my")
    public List<PostResponse> getList(AuthenticatedMember authenticatedMember, @ModelAttribute PostSearch postSearch) {
        return postService.getPostListOfMember(authenticatedMember.getId(), postSearch);
    }

    @GetMapping("/posts/{postId}/questions")
    public List<QuestionResponse> getQuestionList(@PathVariable Long postId) {
        return questionService.getQuestionListOfPost(postId);
    }

    @PatchMapping("/posts/{postId}")
    public PostResponse edit(AuthenticatedMember authenticatedMember, @PathVariable Long postId,
                             @RequestBody PostEdit postEdit) {

        PostEditDto postEditDto = postEdit.toPostEditDtoBuilder()
                .memberId(authenticatedMember.getId())
                .postId(postId)
                .build();
        return postService.edit(postEditDto);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<JsonNode> delete(AuthenticatedMember authenticatedMember, @PathVariable Long postId) {
        postService.delete(authenticatedMember.getId(), postId);
        return extractorService.deleteAnalysisByPostId(authenticatedMember.getId(), postId);
    }
}
