package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.QuestionDeleteDto;
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
        QuestionDeleteDto questionDeleteDto = questionService.delete(questionId, authenticatedMember);
        return extractorService.deleteAnalysisByQuestionFilename(
                authenticatedMember.getId(), questionDeleteDto.getPostId(), questionDeleteDto.getFilename());
    }
}
