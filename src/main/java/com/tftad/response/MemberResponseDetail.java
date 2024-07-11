package com.tftad.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MemberResponseDetail {
    private final MemberResponse member;
    private final List<ChannelResponse> channels;

    public MemberResponseDetail(MemberResponse memberResponse, List<ChannelResponse> channelResponses) {
        this.member = memberResponse;
        this.channels = channelResponses;
    }
}
