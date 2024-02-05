package com.tftad.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorValidation {
    private final String field;
    private final String message;

    @Builder
    public ErrorValidation(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
