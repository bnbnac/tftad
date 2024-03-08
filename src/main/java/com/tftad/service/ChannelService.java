package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.domain.Channel;
import com.tftad.domain.ChannelData;
import com.tftad.domain.Member;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelData generateChannelData(JsonNode channelResource) {
        JsonNode channelItem = channelResource.get("items").get(0);
        String youtubeChannelId = channelItem.get("id").asText();
        String channelTitle = channelItem.get("snippet").get("title").asText();

        return ChannelData.builder()
                .title(channelTitle)
                .youtubeChannelId(youtubeChannelId)
                .build();
    }

    public void validateAddedChannel(String youtubeChannelId) {
        channelRepository.findByYoutubeChannelId(youtubeChannelId)
                .ifPresent(c -> {
                    throw new InvalidRequest(
                            "youtubeChannelId", "이미 등록된 채널입니다. channelId: " + c.getId()
                    );
                });
    }

    public void validateChannelOwner(Member member, String youtubeChannelId) {
        Channel channel = channelRepository.findByYoutubeChannelId(youtubeChannelId)
                .orElseThrow(ChannelNotFound::new);

        if (!member.getId().equals(channel.getMember().getId())) {
            throw new InvalidRequest("url", "계정에 유튜브 채널을 등록해주세요");
        }
    }

    public Long saveChannel(Member member, ChannelData channelData) {
        Channel channel = Channel.builder()
                .youtubeChannelId(channelData.getYoutubeChannelId())
                .title(channelData.getTitle())
                .member(member)
                .build();
        return channelRepository.save(channel).getId();
    }
}
