package com.tftad.service;

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

    private final ChannelRepository channelRepository;
    private final MemberService memberService;

    public boolean isNewChannel(ChannelCreateDto channelCreateDto) {
        return channelRepository.findByYoutubeChannelId(channelCreateDto.getYoutubeChannelId()).isEmpty();
    }

    private void validateChannelOwnerByYoutubeChannelId(Channel channel) {
        Long ownerId = channel.getMember().getId();
        if (!ownerId.equals(-1L)) {
            throw new InvalidRequest(
                    "youtubeChannelId",
                    "이미 등록된 채널입니다. member id: " + ownerId
            );
        }
    }

    public Long validateChannelOwnerByYoutubeChannelId(Long memberId, String youtubeChannelId) {
        Channel channel = channelRepository.findByYoutubeChannelId(youtubeChannelId).orElseThrow(ChannelNotFound::new);

        if (!memberId.equals(channel.getMember().getId())) {
            throw new InvalidRequest(
                    "youtubeChannelId",
                    "계정에 유튜브 채널을 등록해주세요. youtube channel id: " + youtubeChannelId
            );
        }
        return channel.getId();
    }

    public void validateChannelOwnerByChannelId(Long memberId, Long channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);

        if (!memberId.equals(channel.getMember().getId())) {
            throw new InvalidRequest(
                    "youtubeChannelId",
                    "계정에 유튜브 채널을 등록해주세요. channel id: " + channelId
            );
        }
    }

    @Transactional
    public Long saveChannel(ChannelCreateDto channelCreateDto) {
        Member member = memberService.getMemberById(channelCreateDto.getMemberId());

        Channel channel = Channel.builder()
                .youtubeChannelId(channelCreateDto.getYoutubeChannelId())
                .channelTitle(channelCreateDto.getChannelTitle())
                .thumbnail(channelCreateDto.getThumbnail())
                .member(member)
                .build();
        return channelRepository.save(channel).getId();
    }

    public Channel getChannelById(Long channelId) {
        return channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);
    }

    @Transactional
    public void delete(Long channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);
        Member DELETED_MEMBER = memberService.getDeletedMember();

        channel.delete(DELETED_MEMBER);
        channelRepository.save(channel);
    }

    @Transactional
    public Long inherit(Long channelId, Long memberId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);
        channel.inherit(memberService.getMemberById(memberId));
        channelRepository.save(channel);
        return channel.getId();
    }

    public Long validateDeletedChannel(ChannelCreateDto channelCreateDto) {
        Channel existChannel = channelRepository.findByYoutubeChannelId(channelCreateDto.getYoutubeChannelId())
                .orElseThrow(ChannelNotFound::new);
        if (!existChannel.getMember().getId().equals(-1L)) {
            throw new InvalidRequest(
                    "youtubeChannelId",
                    "이미 등록된 채널입니다. member id: " + existChannel.getMember().getId()
            );
        }
        return existChannel.getId();
    }
}
