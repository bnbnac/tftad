package com.tftad.exception;

public class UserNotFound extends TftadException {

    private static final String MESSAGE = "존재하지 않는 멤버입니다";

    public UserNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}