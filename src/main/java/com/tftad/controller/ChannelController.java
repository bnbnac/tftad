package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.ChannelCreateDto;
import com.tftad.service.ChannelService;
import com.tftad.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final OAuthService oAuthService;

    @PostMapping("/channels")
    public void addChannel(AuthenticatedMember authenticatedMember, @RequestParam String code) {
        JsonNode channelResource = oAuthService.queryChannelResource(code);
        ChannelCreateDto channelCreateDto = channelService.createChannelCreateDto(channelResource);

        channelService.addChannel(channelCreateDto, authenticatedMember);
    }

    @DeleteMapping("/channels/{channelId}")
    public void delete(AuthenticatedMember authenticatedMember, @PathVariable Long channelId) {
        channelService.deleteChannel(channelId, authenticatedMember);
    }
}
