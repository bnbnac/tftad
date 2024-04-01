package com.tftad.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.OAuthedMember;
import com.tftad.domain.ChannelCreateDto;
import com.tftad.service.ChannelService;
import com.tftad.service.OAuthService;
import com.tftad.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;
    private final OAuthService oAuthService;
    private final PostService postService;

    @PostMapping("/channels")
    public void addChannel(OAuthedMember oAuthedMember) {
        processChannelAddition(oAuthedMember.getId(), oAuthedMember.getAuthorizationCode());
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

    private void processChannelAddition(Long memberId, String code) {
        String accessToken = oAuthService.queryAccessToken(code);
        JsonNode channelResource = oAuthService.queryChannelResource(accessToken);
        ChannelCreateDto channelCreateDto = createChannelCreateDto(memberId, channelResource);

        if (channelService.isNewChannel(channelCreateDto)) {
            channelService.saveChannel(channelCreateDto);
            return;
        }

        Long deletedChannelId = channelService.validateDeletedChannel(channelCreateDto);

        postService.inheritChannel(deletedChannelId, memberId);
        channelService.inherit(deletedChannelId, memberId);
    }

    @PostMapping("/channels/direct")
    public void addChannelDirectly(AuthenticatedMember authenticatedMember, @RequestParam String code) {
        processChannelAddition(authenticatedMember.getId(), code);
    }

    @DeleteMapping("/channels/{channelId}")
    public void delete(AuthenticatedMember authenticatedMember, @PathVariable Long channelId) {
        channelService.validateChannelOwnerByChannelId(authenticatedMember.getId(), channelId);
        postService.deleteChannel(channelId);
        channelService.delete(channelId);
    }
}
