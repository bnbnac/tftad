package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.ChannelCreateDto;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final AuthService authService;
    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository; direc

    @Transactional
    public void addChannel(ChannelCreateDto channelCreateDto, AuthenticatedMember authenticatedMember) {
        Member member = authService.checkMember(authenticatedMember);

        if (isNewChannel(channelCreateDto)) {
            saveChannel(channelCreateDto, member);
            return;
        }

        Long channelId = validateRegisteredChannel(channelCreateDto.getYoutubeChannelId());
        inheritPostsOfChannel(channelId, member);
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

    private void inheritPostsOfChannel(Long channelId, Member member) { 방향문제
        List<Post> posts = postRepository.findByChannelId(channelId);
        for (Post post : posts) {
            post.inherit(member.getId());
            postRepository.save(post);
        }
    }

    private void inheritChannel(Long channelId, Member member) {
        Channel channel = findChannel(channelId);
        channel.inherit(member.getId());
        channelRepository.save(channel);
    }

    private Channel findChannel(Long channelId) {
        return channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);
    }

    @Transactional
    public void deleteChannel(Long channelId, AuthenticatedMember authenticatedMember) {
        Member member = authService.checkMember(authenticatedMember);
        Channel channel = findChannel(channelId);
        validateChannelOwner(member.getId(), channel);

        Member DELETED_MEMBER = getDeletedMember();
        inheritPostsOfChannel(channelId, DELETED_MEMBER); 방향문제
        inheritChannel(channelId, DELETED_MEMBER);
    }

    private void validateChannelOwner(Long memberId, Channel channel) {
        if (!channel.isOwnedBy(memberId)) {
            throw new InvalidRequest("channel", "소유자가 아닙니다");
        }
    }

    private Member getDeletedMember() {
        return memberRepository.findById(-1L)
                .orElseThrow(() -> new InvalidRequest("memberId", "no member with id -1"));
    }
}
