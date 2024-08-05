package com.tftad.exception;

public class UnmatchedToken extends TftadException {

    private static final String MESSAGE = "토큰이 일치하지 않습니다";

    public UnmatchedToken() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
