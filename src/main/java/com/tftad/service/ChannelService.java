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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberService memberService;

    public Optional<Channel> validateDeletedChannel(String youtubeChannelId) {
        Optional<Channel> channelOptional = channelRepository.findByYoutubeChannelId(youtubeChannelId);
        if (channelOptional.isEmpty()) {
            return Optional.empty();
        }

        validateChannelOwner(channelOptional.get());
        return channelOptional;
    }

    private void validateChannelOwner(Channel channel) {
        Long ownerId = channel.getMember().getId();
        if (!ownerId.equals(-1L)) {
            throw new InvalidRequest(
                    "youtubeChannelId",
                    "이미 등록된 채널입니다. member id: " + ownerId
            );
        }
    }

    public Long validateChannelOwner(Long memberId, String youtubeChannelId) {
        Channel channel = channelRepository.findByYoutubeChannelId(youtubeChannelId).orElseThrow(ChannelNotFound::new);

        if (!memberId.equals(channel.getMember().getId())) {
            throw new InvalidRequest(
                    "youtubeChannelId",
                    "계정에 유튜브 채널을 등록해주세요. youtube channel id: " + youtubeChannelId
            );
        }
        return channel.getId();
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
    public void delete(Long memberId, Long channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);

        if (!memberId.equals(channel.getMember().getId())) {
            throw new InvalidRequest("channel", "채널 소유자만 채널을 삭제할 수 있습니다");
        }

        Member DELETED_MEMBER = memberService.getDeletedMember();
        channel.delete(DELETED_MEMBER);
        channelRepository.save(channel);
    }

    public Long inherit(Channel deletedChannel, Long memberId) {
        deletedChannel.inherit(memberService.getMemberById(memberId));
        channelRepository.save(deletedChannel);
        return deletedChannel.getId();
    }
}
