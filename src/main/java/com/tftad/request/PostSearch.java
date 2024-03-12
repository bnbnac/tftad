package com.tftad.request;

import lombok.Builder;
import lombok.Getter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
public class PostSearch {

    private final Integer MAX_SIZE = 2000;

    private Integer page;
    private Integer size;

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

    @Builder
    public PostSearch(Integer page, Integer size) {
        this.page = page;
        this.size = size;

        if (page == null) {
            this.page = 1;
        }

        if (size == null) {
            this.size = 10;
        }
    }
}
