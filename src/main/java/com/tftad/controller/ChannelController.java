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

    private ChannelCreateDto createChannelCreateDto(Long memberId, JsonNode channelResource) {
        JsonNode channelItem = channelResource.get("items").get(0);
        String youtubeChannelId = channelItem.get("id").asText();
        String channelTitle = channelItem.get("snippet").get("title").asText();
        String thumbnail = channelItem.get("snippet").get("thumbnails").get("default").get("url").asText();

        return ChannelCreateDto.builder()
                .youtubeChannelId(youtubeChannelId)
                .channelTitle(channelTitle)
                .memberId(memberId)
                .thumbnail(thumbnail)
                .build();
    }

    @PostMapping("/channels")
    public void addChannel(AuthenticatedMember authenticatedMember, @RequestParam String code) {
        JsonNode channelResource = oAuthService.queryChannelResource(code);
        ChannelCreateDto channelCreateDto = createChannelCreateDto(authenticatedMember.getId(), channelResource);

        channelService.addChannel(channelCreateDto);
    }

    @DeleteMapping("/channels/{channelId}")
    public void delete(AuthenticatedMember authenticatedMember, @PathVariable Long channelId) {
        channelService.deleteChannel(authenticatedMember.getId(), channelId);
    }
}
