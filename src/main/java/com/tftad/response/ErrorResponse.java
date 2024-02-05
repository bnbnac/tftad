package com.tftad.response;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<ErrorValidation> validations = new ArrayList<>();

    @Builder
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addValidation(ErrorValidation validation) {
        validations.add(validation);
    }
}
