package com.tftad.exception;

public class ExtractorServerError extends TftadException {

    private static final String MESSAGE = "Extractor 서버에 에러가 발생했습니다";

    public ExtractorServerError() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 500; // extractor 코드 받아서 parameter 넣는다?
    }
}
