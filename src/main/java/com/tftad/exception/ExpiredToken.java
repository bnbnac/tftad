package com.tftad.exception;

public class ExpiredToken extends TftadException {

    private static final String MESSAGE = "만료된 토큰입니다";

    public ExpiredToken() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
