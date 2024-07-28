package com.tftad.exception;

public class Unauthorized extends TftadException {

    private static final String MESSAGE = "인증이 필요합니다";

    public Unauthorized() {
        super(MESSAGE);
    }

    public Unauthorized(String msg) {
        super(msg);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
