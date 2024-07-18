package com.tftad.exception;

public class CodeNotFound extends TftadException {

    private static final String MESSAGE = "인증코드가 등록되지 않았습니다";

    public CodeNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
