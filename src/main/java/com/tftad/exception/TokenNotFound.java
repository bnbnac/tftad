package com.tftad.exception;

public class TokenNotFound extends TftadException {

    private static final String MESSAGE = "토큰이 존재하지 않습니다";

    public TokenNotFound() {
        super(MESSAGE);
    }


    @Override
    public int getStatusCode() {
        return 401;
    }
}
