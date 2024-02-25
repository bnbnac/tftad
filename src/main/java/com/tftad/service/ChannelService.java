package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.OAuthedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final OAuthService oAuthService;

    @Transactional
    public void addChannel(OAuthedMember oAuthedMember) {
        Member member = memberRepository.findById(oAuthedMember.getId())
                .orElseThrow(MemberNotFound::new);

        JsonNode channelResource = oAuthService
                .queryChannelResource(oAuthedMember.getAuthorizationCode());
        Channel channel = generateChannel(channelResource);

        channel.changeMember(member);
        channelRepository.save(channel);
    }

    private Channel generateChannel(JsonNode channelResource) {
        JsonNode channelItem = channelResource.get("items").get(0);

        String youtubeChannelId = channelItem.get("id").asText();
        validateUniqueChannel(youtubeChannelId);

        String channelTitle = channelItem.get("snippet").get("title").asText();

        return Channel.builder()
                .youtubeChannelId(youtubeChannelId)
                .title(channelTitle)
                .build();
    }

    private void validateUniqueChannel(String youtubeChannelId) {
        Optional<Channel> presentChannel = channelRepository.findByYoutubeChannelId(youtubeChannelId);
        if (presentChannel.isPresent()) {
            throw new InvalidRequest("youtubeChannelId", "이미 등록된 채널입니다");
        }
    }
}
