package com.tftad.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelData {

    private final String title;
    private final String youtubeChannelId;
}
