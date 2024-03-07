package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Post;
import com.tftad.response.PositionOfPostResponse;
import com.tftad.response.external.ExtractorCompletion;
import com.tftad.service.ExtractorService;
import com.tftad.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExtractorController {

    private final ExtractorService extractorService;
    private final PostService postService;

    @PostMapping("/extractor/complete")
    public ResponseEntity<String> saveQuestion(@RequestBody ExtractorCompletion extractorCompletion) {
        Long postId = extractorCompletion.getPostId();
        List<String> result = extractorCompletion.getResult();

        if (extractorService.validatePostInExtractorCompletion(postId)) {
            return ResponseEntity.badRequest().body("POST NOT FOUND or POST IS PUBLISHED ALREADY");
        }

        if (extractorCompletion.getResult().isEmpty()) {
            postService.delete(postId);
            return ResponseEntity.ok("server got empty result. post "
                    + extractorCompletion.getPostId() + " has been deleted");
        }

        extractorService.generateQuestions(postId, result);

        return ResponseEntity.ok("question data saved");
    }

    @GetMapping("/extractor/position/{postId}")
    public PositionOfPostResponse getPosition(AuthenticatedMember authenticatedMember, @PathVariable Long postId) {
        Post post = extractorService.validatePostBeforeGetPosition(authenticatedMember.getId(), postId);
        return extractorService.getPosition(post);
    }
}
