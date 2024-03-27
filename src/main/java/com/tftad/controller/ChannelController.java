package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.OAuthedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.ChannelCreateDto;
import com.tftad.service.ChannelService;
import com.tftad.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final OAuthService oAuthService;

    @PostMapping("/channels")
    public Long addChannel(OAuthedMember oAuthedMember) {
        return processChannelAddition(oAuthedMember.getId(), oAuthedMember.getAuthorizationCode());
    }

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

    private Long processChannelAddition(Long memberId, String code) {
        String accessToken = oAuthService.queryAccessToken(code);
        JsonNode channelResource = oAuthService.queryChannelResource(accessToken);

        ChannelCreateDto channelCreateDto = createChannelCreateDto(memberId, channelResource);
        Optional<Channel> deletedChannel = channelService
                .validateDeletedChannel(channelCreateDto.getYoutubeChannelId());

        if (deletedChannel.isPresent()) {
            return channelService.inherit(deletedChannel.get(), memberId);
        }
        return channelService.saveChannel(channelCreateDto);
    }

    @PostMapping("/channels/direct")
    public Long addChannelDirectly(AuthenticatedMember authenticatedMember, @RequestParam String code) {
        return processChannelAddition(authenticatedMember.getId(), code);
    }

    @DeleteMapping("/channels/{channelId}")
    public void delete(AuthenticatedMember authenticatedMember, @PathVariable Long channelId) {
        channelService.delete(authenticatedMember.getId(), channelId);
    }
}
