package com.tftad.exception;

public class ChannelNotFound extends TftadException {

    private static final String MESSAGE = "등록되지 않은 채널입니다";

    public ChannelNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
