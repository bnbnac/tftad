package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Question;
import com.tftad.service.ExtractorService;
import com.tftad.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final ExtractorService extractorService;

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<JsonNode> delete(AuthenticatedMember authenticatedMember, @PathVariable Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        String filename = question.generateFilename();
        Long postId = question.getPost().getId();

        questionService.delete(authenticatedMember.getId(), questionId);
        return extractorService.deleteAnalysisByQuestionFilename(authenticatedMember.getId(), postId, filename);
    }
}
