package com.tftad.exception;

public class QuestionNotFound extends TftadException {

    private static final String MESSAGE = "존재하지 않는 문제입니다";

    public QuestionNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
