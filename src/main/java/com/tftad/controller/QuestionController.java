package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.QuestionDeleteDto;
import com.tftad.domain.QuestionEditDto;
import com.tftad.request.QuestionEdit;
import com.tftad.response.QuestionResponse;
import com.tftad.service.ExtractorService;
import com.tftad.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final ExtractorService extractorService;

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<JsonNode> delete(AuthenticatedMember authenticatedMember, @PathVariable Long questionId) {
        QuestionDeleteDto delete = questionService.delete(authenticatedMember.getId(), questionId);
        return extractorService.deleteAnalysisByQuestionFilename(
                authenticatedMember.getId(), delete.getPostId(), delete.getFilename());
    }

    @PatchMapping("/questions/{questionId}")
    public QuestionResponse edit(AuthenticatedMember authenticatedMember, @PathVariable Long questionId,
            @RequestBody QuestionEdit questionEdit) {

        QuestionEditDto questionEditDto = questionEdit.toQuestionEditDtoBuilder()
                .memberId(authenticatedMember.getId())
                .questionId(questionId)
                .build();
        return questionService.edit(questionEditDto);
    }
}
