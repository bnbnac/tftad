package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.ChannelCreateDto;
import com.tftad.domain.Member;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final AuthService authService;
    private final ChannelRepository channelRepository;
    private final MemberService memberService;
    private final ChannelInheritService channelInheritService;

    @Transactional
    public void addChannel(ChannelCreateDto channelCreateDto, AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);

        if (isNewChannel(channelCreateDto)) {
            saveChannel(channelCreateDto, member);
            return;
        }

        Long channelId = validateRegisteredChannel(channelCreateDto.getYoutubeChannelId());
        channelInheritService.inheritPostsOfChannel(channelId, member.getId());
        inheritChannel(channelId, member);
    }

    private boolean isNewChannel(ChannelCreateDto channelCreateDto) {
        return channelRepository.findByYoutubeChannelId(channelCreateDto.getYoutubeChannelId()).isEmpty();
    }

    private void saveChannel(ChannelCreateDto channelCreateDto, Member member) {
        Channel channel = createChannel(channelCreateDto, member.getId());
        channelRepository.save(channel);
    }

    private Channel createChannel(ChannelCreateDto channelCreateDto, Long memberId) {
        return Channel.builder()
                .youtubeChannelId(channelCreateDto.getYoutubeChannelId())
                .channelTitle(channelCreateDto.getChannelTitle())
                .thumbnail(channelCreateDto.getThumbnail())
                .memberId(memberId)
                .build();
    }

    private Long validateRegisteredChannel(String youtubeChannelId) {
        Channel channel = channelRepository.findByYoutubeChannelId(youtubeChannelId).orElseThrow(ChannelNotFound::new);
        if (!channel.getMemberId().equals(-1L)) {
            throw new InvalidRequest("youtubeChannelId", "이미 등록된 채널입니다");
        }
        return channel.getId();
    }

    private void inheritChannel(Long channelId, Member member) {
        Channel channel = findChannel(channelId);
        channel.inherit(member.getId());
    }

    private Channel findChannel(Long channelId) {
        return channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);
    }

    @Transactional
    public void deleteChannel(Long channelId, AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);
        Channel channel = findChannel(channelId);
        validateChannelOwner(member.getId(), channel);

        Member DELETED_MEMBER = memberService.getDeletedMember();
        channelInheritService.inheritPostsOfChannel(channelId, DELETED_MEMBER.getId());
        inheritChannel(channelId, DELETED_MEMBER);
    }

    private void validateChannelOwner(Long memberId, Channel channel) {
        if (!channel.isOwnedBy(memberId)) {
            throw new InvalidRequest("channel", "소유자가 아닙니다");
        }
    }

    public ChannelCreateDto createChannelCreateDto(JsonNode channelResource) {
        try {
            JsonNode channelItem = channelResource.get("items").get(0);
            String youtubeChannelId = channelItem.get("id").asText();
            String channelTitle = channelItem.get("snippet").get("title").asText();
            String thumbnail = channelItem.get("snippet").get("thumbnails").get("default").get("url").asText();

            return ChannelCreateDto.builder()
                    .youtubeChannelId(youtubeChannelId)
                    .channelTitle(channelTitle)
                    .thumbnail(thumbnail)
                    .build();
        } catch (NullPointerException e) {
            throw new InvalidRequest("oauth", "failed to get youtube channel information");
        }
    }
}
