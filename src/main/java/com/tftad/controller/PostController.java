package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Member;
import com.tftad.exception.ExtractorServerError;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import com.tftad.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final OAuthService oAuthService;
    private final MemberService memberService;
    private final ChannelService channelService;
    private final ExtractorService extractorService;

    @PostMapping("/posts")
    public Long post(AuthenticatedMember authenticatedMember, @RequestBody @Valid PostCreate postCreate) {
        postCreate.fillVideoIdFromVideoUrl();
        Member member = memberService.getMemberById(authenticatedMember.getId());
        String youtubeChannelId = oAuthService.queryVideoResourceToGetChannelId(postCreate.getVideoId());

        channelService.validateChannelOwner(member, youtubeChannelId);
        postService.validatePostedVideo(postCreate.getVideoId());
        Long postId = postService.savePost(member, postCreate);

        queryToExtractor(postCreate.getVideoId(), postId);
        return postId;
    }

    private void queryToExtractor(String videoId, Long postId) {
        try {
            extractorService.queryAnalysis(videoId, postId);
        } catch (Exception e) {
            postService.delete(postId);
            throw new ExtractorServerError();
        }
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public PostResponse edit(@PathVariable Long postId, @RequestBody PostEdit postEdit) {
        return postService.edit(postId, postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }
}
