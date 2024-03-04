package com.tftad.exception;

public class ChannelNotFound extends TftadException {

    private static final String MESSAGE = "존재하지 않는 채널입니다";

    public ChannelNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
