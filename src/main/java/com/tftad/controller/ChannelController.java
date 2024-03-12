package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.OAuthedMember;
import com.tftad.domain.ChannelCreateDto;
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
    private final OAuthService oAuthService;
    private final MemberService memberService;

    @PostMapping("/oauth/add/channel")
    public Long addChannel(OAuthedMember oAuthedMember) {
        Long memberId = memberService.getMemberById(oAuthedMember.getId()).getId();
        String accessToken = oAuthService.queryAccessToken(oAuthedMember.getAuthorizationCode());
        JsonNode channelResource = oAuthService.queryChannelResource(accessToken);

        ChannelCreateDto channelCreateDto = createChannelCreateDto(memberId, channelResource);
        channelService.validateAddedChannel(channelCreateDto.getYoutubeChannelId());
        return channelService.saveChannel(channelCreateDto);
    }

    private ChannelCreateDto createChannelCreateDto(Long memberId, JsonNode channelResource) {
        JsonNode channelItem = channelResource.get("items").get(0);
        String youtubeChannelId = channelItem.get("id").asText();
        String channelTitle = channelItem.get("snippet").get("title").asText();

        return ChannelCreateDto.builder()
                .youtubeChannelId(youtubeChannelId)
                .channelTitle(channelTitle)
                .memberId(memberId)
                .build();
    }
}
