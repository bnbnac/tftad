package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.QuestionDeleteDto;
import com.tftad.response.QuestionResponse;
import com.tftad.service.ExtractorService;
import com.tftad.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final ExtractorService extractorService;

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<JsonNode> delete(AuthenticatedMember authenticatedMember, @PathVariable Long questionId) {
        QuestionDeleteDto questionDeleteDto = questionService.delete(questionId, authenticatedMember);
        return extractorService.deleteAnalysisByQuestionFilename(
                authenticatedMember.getId(), questionDeleteDto.getPostId(), questionDeleteDto.getFilename());
    }

    @GetMapping("/posts/{postId}/questions")
    public List<QuestionResponse> getQuestionList(@PathVariable Long postId) {
        return questionService.getListOf(postId);
    }
}
