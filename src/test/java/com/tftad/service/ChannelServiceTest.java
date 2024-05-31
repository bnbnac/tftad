package com.tftad.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tftad.TestUtility;
import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ChannelServiceTest {

    @Autowired
    private TestUtility testUtility;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    PostRepository postRepository;

    static private JsonNode channelResource;

    Member createMember() {
        return testUtility.createMember();
    }

    Channel createChannel(Member member) {
        return testUtility.createChannel(member);
    }

    Post createPost(Member member, Channel channel) {
        return testUtility.createPost(member, channel);
    }

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        String jsonStr = "{\"items\":[{\"id\":\"testId\",\"snippet\":{\"title\":\"testTitle\"}}]}";
        ObjectMapper objectMapper = new ObjectMapper();
        channelResource = objectMapper.readTree(jsonStr);
    }

    @BeforeEach
    void clean() {
        questionRepository.deleteAll();
        postRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("validateDeletedChannel 테스트: 등록된 채널")
    void test1() {
        Member member = createMember();
        Channel channel = createChannel(member);

        // when then
        assertThrows(InvalidRequest.class, () -> {
            channelService.validateRegisteredChannel("channelId");
        });
    }

    @Test
    @DisplayName("validateDeletedChannel 테스트: 새로운 채널")
    void test2() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Channel channel = Channel.builder()
                .youtubeChannelId("channelId")
                .channelTitle("title")
                .member(member)
                .build();
        channelRepository.save(channel);

        // when then
        assertDoesNotThrow(() -> channelService.validateRegisteredChannel("channelId" + "other"));
    }

    @Test
    @DisplayName("validateChannelOwner 테스트: 소유자인 경우")
    void test3() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Channel channel = Channel.builder()
                .youtubeChannelId("channelId")
                .channelTitle("title")
                .member(member)
                .build();
        channelRepository.save(channel);

        // when then
        assertDoesNotThrow(() -> channelService.validateChannelOwnerByYoutubeChannelId(member.getId(), "channelId"));
    }
//
//    @Test
//    @DisplayName("validateChannelOwner 테스트: 등록되지 않은 채널")
//    void test4() {
//        Member member = Member.builder()
//                .email("email")
//                .password("pswd")
//                .name("name")
//                .build();
//        memberRepository.save(member);
//
//        Channel channel = Channel.builder()
//                .youtubeChannelId("channelId")
//                .channelTitle("title")
//                .member(member)
//                .build();
//        channelRepository.save(channel);
//
//        // when then
//        assertThrows(ChannelNotFound.class, () -> {
//            channelService.validateChannelOwnerByYoutubeChannelId(member.getId(), "channelId" + "other");
//        });
//    }
//
//    @Test
//    @DisplayName("validateChannelOwner 테스트: 등록된 채널이지만 소유하지 않은 경우")
//    void test5() {
//        Member member = Member.builder()
//                .email("email")
//                .password("pswd")
//                .name("name")
//                .build();
//        memberRepository.save(member);
//        Member otherMember = Member.builder()
//                .email("email")
//                .password("pswd")
//                .name("name")
//                .build();
//        memberRepository.save(otherMember);
//
//        Channel channel = Channel.builder()
//                .youtubeChannelId("channelId")
//                .channelTitle("title")
//                .member(member)
//                .build();
//        channelRepository.save(channel);
//
//        // when then
//        assertThrows(InvalidRequest.class, () -> {
//            channelService.validateChannelOwnerByYoutubeChannelId(otherMember.getId(), "channelId");
//        });
//    }
//
//
//    @Test
//    @DisplayName("channel 저장")
//    void test6() {
//        Member member = Member.builder()
//                .email("email")
//                .password("pswd")
//                .name("name")
//                .build();
//        Long memberId = memberRepository.save(member).getId();
//
//        ChannelCreateDto channelCreateDto = ChannelCreateDto.builder()
//                .memberId(memberId)
//                .channelTitle("title")
//                .youtubeChannelId("cid")
//                .build();
//
//        // when
//        channelService.saveChannel(channelCreateDto);
//
//        // then
//        assertEquals(1L, channelRepository.count());
//        Channel channel = channelRepository.findAll().iterator().next();
//        assertEquals("title", channel.getChannelTitle());
//        assertEquals("cid", channel.getYoutubeChannelId());
//    }
}