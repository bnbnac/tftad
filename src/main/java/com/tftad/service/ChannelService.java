package com.tftad.service;

import com.tftad.domain.Channel;
import com.tftad.domain.ChannelCreateDto;
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

    public void validateAddedChannel(String youtubeChannelId) {
        channelRepository.findByYoutubeChannelId(youtubeChannelId)
                .ifPresent(c -> {
                    throw new InvalidRequest(
                            "youtubeChannelId", "이미 등록된 채널입니다. channelId: " + c.getId()
                    );
                });
    }

    public void validateChannelOwner(Member member, String youtubeChannelId) {
        Channel channel = channelRepository.findByYoutubeChannelId(youtubeChannelId).orElseThrow(ChannelNotFound::new);

        if (!member.getId().equals(channel.getMember().getId())) {
            throw new InvalidRequest("url", "계정에 유튜브 채널을 등록해주세요");
        }
    }

    public Long saveChannel(ChannelCreateDto channelCreateDto) {
        Channel channel = Channel.builder()
                .youtubeChannelId(channelCreateDto.getYoutubeChannelId())
                .channelTitle(channelCreateDto.getChannelTitle())
                .member(channelCreateDto.getMember())
                .build();
        return channelRepository.save(channel).getId();
    }
}
