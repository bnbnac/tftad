package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.OAuthedMember;
import com.tftad.domain.ChannelData;
import com.tftad.domain.Member;
import com.tftad.service.ChannelService;
import com.tftad.service.MemberService;
import com.tftad.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final MemberService memberService;
    private final OAuthService oAuthService;

    @PostMapping("/oauth/add/channel")
    public Long addChannel(OAuthedMember oAuthedMember) {
        Member member = memberService.getMemberById(oAuthedMember.getId());

        JsonNode channelResource = oAuthService
                .queryChannelResource(oAuthedMember.getAuthorizationCode());

        ChannelData channelData = channelService.generateChannelData(channelResource);
        channelService.validateAddedChannel(channelData.getYoutubeChannelId());

        return channelService.saveChannel(member, channelData);
    }
}
