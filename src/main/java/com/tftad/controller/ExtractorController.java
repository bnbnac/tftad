package com.tftad.controller;

import com.tftad.domain.Post;
import com.tftad.request.ExtractorCompletion;
import com.tftad.service.ExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExtractorController {

    private final ExtractorService extractorService;

    @PostMapping("/extractor/complete")
    public ResponseEntity<String> getResult(@RequestBody ExtractorCompletion extractorCompletion) {
        Long postId = extractorCompletion.getPostId();
        List<String> result = extractorCompletion.getResult();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: 'result' list cannot be empty.");
        }

        Post post = extractorService.getPost(postId);
        extractorService.generateQuestions(post, result);

        //transaction 관리?

        return ResponseEntity.ok("Processing complete successfully.");
    }
}
