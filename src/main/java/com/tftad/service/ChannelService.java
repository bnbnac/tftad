package com.tftad.service;

import com.tftad.domain.Channel;
import com.tftad.domain.ChannelCreateDto;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
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

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostService postService;

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

    public Long saveChannel(ChannelCreateDto channelCreateDto) {
        Member member = memberRepository.findById(channelCreateDto.getMemberId()).orElseThrow(MemberNotFound::new);

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

    public void inheritChannel(Long channelId, Long memberId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFound::new);
        channel.inherit(member);
        channelRepository.save(channel);
    }

    public Long validateRegisteredChannel(String youtubeChannelId) {
        Channel channel = channelRepository.findByYoutubeChannelId(youtubeChannelId).orElseThrow(ChannelNotFound::new);
        if (!channel.getMember().getId().equals(-1L)) {
            throw new InvalidRequest("youtubeChannelId", "이미 등록된 채널입니다.");
        }
        return channel.getId();
    }

    @Transactional
    public void addChannel(ChannelCreateDto channelCreateDto) {
        if (isNewChannel(channelCreateDto)) {
            saveChannel(channelCreateDto);
            return;
        }

        Long channelId = validateRegisteredChannel(channelCreateDto.getYoutubeChannelId());




        List<Post> posts = postRepository.findByChannelId(channelId);
        Member member = memberRepository.findById(channelCreateDto.getMemberId()).orElseThrow(MemberNotFound::new);

        for (Post post : posts) {
            post.inherit(member);
            postRepository.save(post);
        }




        inheritChannel(channelId, channelCreateDto.getMemberId());
    }

    @Transactional
    public void deleteChannel(Long memberId, Long channelId) {
        Channel channel = findChannel(channelId);
        validateChannelOwner(memberId, channel);
        Member DELETED_MEMBER = getDeletedMember();

        List<Post> posts = channel.getPosts();
        for (Post post : posts) {
            post.inherit(DELETED_MEMBER);
            postRepository.save(post);
        }

        channel.inherit(DELETED_MEMBER);
        channelRepository.save(channel);
    }

    private Channel findChannel(Long channelId) {
        return channelRepository.findById(channelId).orElseThrow(ChannelNotFound::new);
    }

    private void validateChannelOwner(Long memberId, Channel channel) {
        if (!channel.isOwnedBy(memberId)) {
            throw new InvalidRequest("channel", "채널의 소유자가 아닙니다");
        }
    }

    private Member getDeletedMember() {
        return memberRepository.findById(-1L)
                .orElseThrow(() -> new InvalidRequest("memberId", "no member with id -1")
                );
    }
}
