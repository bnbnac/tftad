package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.PostCreateDto;
import com.tftad.exception.ExtractorServerError;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import com.tftad.response.PostResponseDetail;
import com.tftad.response.QuestionResponse;
import com.tftad.service.ExtractorService;
import com.tftad.service.PostService;
import com.tftad.service.QuestionService;
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
    private final ExtractorService extractorService;
    private final QuestionService questionService;

    @PostMapping("/posts")
    public void post(AuthenticatedMember authenticatedMember, @RequestBody @Valid PostCreate postCreate) {
        PostCreateDto postCreateDto = createPostCreateDto(postCreate);
        Long postId = postService.write(postCreateDto, authenticatedMember);

        queryToExtractor(postCreateDto.getVideoId(), authenticatedMember, postId);
    }

    private PostCreateDto createPostCreateDto(PostCreate postCreate) {
        String videoId = extractVideoId(postCreate.getVideoUrl());
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
    public List<PostResponse> getMyList(AuthenticatedMember authenticatedMember, @ModelAttribute PostSearch postSearch) {
        return postService.getListOf(authenticatedMember.getId(), postSearch);
    }

    @GetMapping("/posts/{postId}/questions")
    public List<QuestionResponse> getQuestionList(@PathVariable Long postId) {
        return questionService.getListOf(postId);
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
}
