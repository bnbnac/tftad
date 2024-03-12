package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.exception.ExtractorServerError;
import com.tftad.exception.InvalidRequest;
import com.tftad.request.ExtractorCompletion;
import com.tftad.response.PositionOfPostResponse;
import com.tftad.service.ExtractorService;
import com.tftad.service.PostService;
import com.tftad.service.QuestionService;
import jakarta.validation.Valid;
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
    public ResponseEntity<String> getExtractorResult(@RequestBody @Valid ExtractorCompletion extractorCompletion) {
        Long postId = extractorCompletion.getPostId();
        List<String> extractorResult = extractorCompletion.getResult();

        try {
            postService.validatePublishedPost(postId);
            postService.validateExtractorResultOrDeletePost(postId, extractorResult);
            questionService.saveQuestionsFromExtractorResult(postId, extractorResult);
            postService.showPost(postId);
            return ResponseEntity.ok("question data saved. post id: " + postId);
        } catch (InvalidRequest e) {
            return ResponseEntity.badRequest().body(e.getMessage() + " post id: " + postId);
        } catch (ExtractorServerError e) {
            return ResponseEntity.ok("server got empty result. post has been deleted. post id: " + postId);
        }
    }

    @GetMapping("/extractor/position/{postId}")
    public PositionOfPostResponse getPosition(AuthenticatedMember authenticatedMember, @PathVariable Long postId) {
        postService.validateToGetPosition(authenticatedMember.getId(), postId);
        return extractorService.getPosition(postId);
    }
}
