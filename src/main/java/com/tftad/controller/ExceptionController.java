package com.tftad.controller;

import com.tftad.exception.InvalidRequest;
import com.tftad.exception.TftadException;
import com.tftad.response.ErrorResponse;
import com.tftad.response.ErrorValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(TftadException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> tftadException(TftadException e) {
        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(e.getStatusCode()))
                .message(e.getMessage())
                .build();

        if (e instanceof InvalidRequest invalidRequest) {
            body.addValidation(invalidRequest.getValidation());
        }

        return ResponseEntity.status(e.getStatusCode())
                .body(body);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            ErrorValidation validation = ErrorValidation.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();

            response.addValidation(validation);
        }

        return response;
    }
}
