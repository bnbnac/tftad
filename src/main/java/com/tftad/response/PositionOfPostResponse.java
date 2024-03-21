package com.tftad.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PositionOfPostResponse {
    private final String curPosition;
    private final String initialPosition;
    private final Boolean published;
    private final String state;
    private final String curFrame;
    private final String totalFrame;

    @Builder
    public PositionOfPostResponse(String curPosition, String initialPosition, String state,
                                  String curFrame, String totalFrame, Boolean published) {
        this.published = published;
        this.curPosition = curPosition;
        this.initialPosition = initialPosition;
        this.curFrame = curFrame;
        this.totalFrame = totalFrame;
        this.state = state;
    }
}