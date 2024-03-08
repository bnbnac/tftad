package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Post;
import com.tftad.exception.ExtractorServerError;
import com.tftad.exception.InvalidRequest;
import com.tftad.response.PositionOfPostResponse;
import com.tftad.response.external.ExtractorCompletion;
import com.tftad.service.ExtractorService;
import com.tftad.service.PostService;
import com.tftad.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExtractorController {

    private final ExtractorService extractorService;
    private final PostService postService;
    private final QuestionService questionService;

    @PostMapping("/extractor/complete")
    public ResponseEntity<String> getExtractorResult(@RequestBody ExtractorCompletion extractorCompletion) {
        Long postId = extractorCompletion.getPostId();
        List<String> extractorResult = extractorCompletion.getResult();

        try {
            Post post = postService.validateCompletedPost(postId);
            validateExtractorResult(postId, extractorResult);
            questionService.saveQuestionsOnThePostFromExtractorResult(post, extractorResult);
            postService.showPost(post);
            return ResponseEntity.ok("question data saved");
        } catch (InvalidRequest e) {
            return ResponseEntity.badRequest().body(e.getMessage() + " post id: " + postId);
        } catch (ExtractorServerError e) {
            return ResponseEntity.ok("server got empty result. post has been deleted. post id: " + postId);
        }
    }

    private void validateExtractorResult(Long postId, List<String> extractorResult) {
        if (extractorResult.isEmpty()) {
            postService.delete(postId);
            throw new ExtractorServerError();
        }
    }

    @GetMapping("/extractor/position/{postId}")
    public PositionOfPostResponse getPosition(AuthenticatedMember authenticatedMember, @PathVariable Long postId) {
        Post post = postService.validatePostBeforeGetPosition(authenticatedMember.getId(), postId);
        return extractorService.getPosition(post);
    }
}
