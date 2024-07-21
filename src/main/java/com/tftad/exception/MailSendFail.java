package com.tftad.exception;

public class MailSendFail extends TftadException {

    private static final String MESSAGE = "메일 발송에 실패했습니다";

    public MailSendFail() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
