package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.exception.ExtractorResultError;
import com.tftad.exception.InvalidRequest;
import com.tftad.request.ExtractorCompletion;
import com.tftad.response.PositionOfPostResponse;
import com.tftad.service.ExtractorService;
import com.tftad.service.PostService;
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

    @PostMapping("/extractor/complete")
    public ResponseEntity<String> getExtractorResult(@RequestBody @Valid ExtractorCompletion extractorCompletion) { // 여기 인증수단을 마련하고 저 밑에 catch문도 해결하자
        Long postId = extractorCompletion.getPostId();
        List<String> extractorResult = extractorCompletion.getResult();

        try {
            postService.processExtractorCompletion(postId, extractorResult);
            return ResponseEntity.ok("question data saved. post id: " + postId);
        } catch (InvalidRequest e) {
            return ResponseEntity.badRequest().body(e.getMessage() + " post id: " + postId);
        } catch (ExtractorResultError e) {
//            extractorService.deleteAnalysisByPostId(memberId, postId);
            return ResponseEntity.ok("server got empty result. post has been deleted. post id: " + postId);
        }
    }

    @GetMapping("/extractor/position/{postId}")
    public PositionOfPostResponse getPosition(AuthenticatedMember authenticatedMember, @PathVariable Long postId) {
        return extractorService.getPosition(postId, authenticatedMember);
    }
}
