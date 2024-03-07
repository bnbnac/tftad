package com.tftad.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PositionOfPostResponse {

    private final Position position;
    private final boolean published;

    @Builder
    public PositionOfPostResponse(Integer initial, Integer current, boolean published) {
        this.position = Position.builder()
                .current(current)
                .initial(initial)
                .build();
        this.published = published;
    }

    @Getter
    public static class Position {
        private final Integer initial;
        private final Integer current;

        @Builder
        public Position(Integer initial, Integer current) {
            this.initial = initial;
            this.current = current;
        }
    }
}