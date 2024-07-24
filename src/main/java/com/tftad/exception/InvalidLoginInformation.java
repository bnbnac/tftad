package com.tftad.exception;

public class InvalidLoginInformation extends TftadException {

    private static String MESSAGE = "이메일/비밀번호가 올바르지 않습니다";

    public InvalidLoginInformation() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
