package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.PostCreateDto;
import com.tftad.exception.ExtractorResultError;
import com.tftad.exception.ExtractorServerError;
import com.tftad.exception.InvalidRequest;
import com.tftad.request.ExtractorCompletion;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PositionOfPostResponse;
import com.tftad.response.PostResponse;
import com.tftad.response.PostResponseDetail;
import com.tftad.service.ExtractorService;
import com.tftad.service.PostService;
import com.tftad.utility.Utility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ExtractorService extractorService;
    private final Utility utility;

    @PostMapping("/posts")
    public void post(AuthenticatedMember authenticatedMember, @RequestBody @Valid PostCreate postCreate) {
        PostCreateDto postCreateDto = createPostCreateDto(postCreate);
        Long postId = postService.write(postCreateDto, authenticatedMember);

        queryToExtractor(postCreateDto.getVideoId(), authenticatedMember, postId);
    }

    private PostCreateDto createPostCreateDto(PostCreate postCreate) {
        String videoId = utility.extractVideoId(postCreate.getVideoUrl());
        return postCreate.toPostCreateDtoBuilder()
                .videoId(videoId)
                .build();
    }

    private void queryToExtractor(String videoId, AuthenticatedMember authenticatedMember, Long postId) {
        try {
            extractorService.getAnalysis(videoId, authenticatedMember.getId(), postId);
        } catch (Exception e) {
            postService.delete(postId, authenticatedMember);
            throw new ExtractorServerError();
        }
    }

    @GetMapping("/posts/{postId}")
    public PostResponseDetail get(@PathVariable Long postId) {
        return postService.getPostDetails(postId);
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
    public List<PostResponse> getMyList(AuthenticatedMember authenticatedMember, @ModelAttribute PostSearch postSearch) {
        return postService.getListOf(authenticatedMember.getId(), postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(AuthenticatedMember authenticatedMember, @PathVariable Long postId,
                     @RequestBody PostEdit postEdit) {
        postService.edit(postId, postEdit, authenticatedMember);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<JsonNode> delete(AuthenticatedMember authenticatedMember, @PathVariable Long postId) {
        postService.delete(postId, authenticatedMember);
        return extractorService.deleteAnalysisByPostId(authenticatedMember.getId(), postId);
    }

    @PostMapping("/extractor/complete")
    public ResponseEntity<String> getExtractorResult(@RequestBody @Valid ExtractorCompletion extractorCompletion) {
        Long postId = extractorCompletion.getPostId();

        try {
            postService.processExtractorCompletion(extractorCompletion);
            return ResponseEntity.ok("question data saved. post id: " + postId);
        } catch (InvalidRequest e) {
            return ResponseEntity.badRequest().body(e.getMessage() + " post id: " + postId);
        } catch (ExtractorResultError e) {
            extractorService.deleteAnalysisByPostId(e.getMemberId(), e.getPostId());
            return ResponseEntity.ok("server got empty result. post has been deleted. post id: " + postId);
        }
    }

    @GetMapping("/extractor/position/{postId}")
    public PositionOfPostResponse getPosition(AuthenticatedMember authenticatedMember, @PathVariable Long postId) {
        if (postService.isPublishedPostOwner(postId, authenticatedMember)) {
            return PositionOfPostResponse.builder()
                    .published(true)
                    .build();
        }

        return extractorService.getPosition(postId);
    }
}
