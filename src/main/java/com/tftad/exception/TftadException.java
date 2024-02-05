package com.tftad.exception;

import com.tftad.response.ErrorValidation;
import lombok.Getter;

@Getter
public abstract class TftadException extends RuntimeException {

    private ErrorValidation validation;

    public TftadException(String message) {
        super(message);
    }

    public abstract int getStatusCode();

    public void setValidation(String field, String message) {
        this.validation = ErrorValidation.builder()
                .field(field)
                .message(message)
                .build();
    }
}
