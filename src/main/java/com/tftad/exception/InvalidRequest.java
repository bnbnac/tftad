package com.tftad.exception;

import lombok.Getter;

@Getter
public class InvalidRequest extends TftadException {

    private static final String MESSAGE = "잘못된 요청입니다";

    private String field;
    private String message;

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String field, String message) {
        super(MESSAGE);
        setValidation(field, message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
