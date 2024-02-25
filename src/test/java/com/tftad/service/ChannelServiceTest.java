package com.tftad.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tftad.config.data.OAuthedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {
    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private OAuthService oAuthService;

    @InjectMocks
    private ChannelService channelService;

    private OAuthedMember oAuthedMember = OAuthedMember
            .builder()
            .id(1L)
            .authorizationCode("code")
            .build();
    private Member member = Member.builder().build();

    static private JsonNode channelResource;

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        String jsonStr = "{\"items\":[{\"id\":\"testId\",\"snippet\":{\"title\":\"testTitle\"}}]}";
        ObjectMapper objectMapper = new ObjectMapper();
        channelResource = objectMapper.readTree(jsonStr);
    }

    @Test
    @DisplayName("멤버에 채널을 추가한다")
    void test1() throws JsonProcessingException {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        when(oAuthService.queryChannelResource("code")).thenReturn(channelResource);

        when(channelRepository.findByYoutubeChannelId(any())).thenReturn(Optional.empty());
        when(channelRepository.save(any(Channel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        channelService.addChannel(oAuthedMember);
        verify(channelRepository, times(1)).save(any(Channel.class));
    }

    @Test
    @DisplayName("존재하지 않는 멤버에 채널을 추가할 수 없다")
    void test2() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFound.class, () -> channelService.addChannel(oAuthedMember));
        verify(channelRepository, never()).save(any(Channel.class));
    }

    @Test
    @DisplayName("이미 등록된 채널은 등록할 수 없다")
    void test3() throws JsonProcessingException {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        when(oAuthService.queryChannelResource("code")).thenReturn(channelResource);

        when(channelRepository.findByYoutubeChannelId(eq("testId")))
                .thenReturn(Optional.of(Channel.builder().build()));

        assertThrows(InvalidRequest.class, () -> channelService.addChannel(oAuthedMember));
        verify(channelRepository, never()).save(any(Channel.class));
    }
}