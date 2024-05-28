package com.tftad;

import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtility {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private PostRepository postRepository;

    public Member createMember() {
        Member member = Member.builder()
                .email("email")
                .password("password")
                .name("name")
                .build();
        return memberRepository.save(member);
    }

    public Channel createChannel(Member member) {
        Channel channel = Channel.builder()
                .thumbnail("thumbnail")
                .youtubeChannelId("youtubeChannelId")
                .channelTitle("channelTitle")
                .member(member)
                .build();
        return channelRepository.save(channel);
    }

    public Post createPost(Member member, Channel channel) {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .channel(channel)
                .build();
        return postRepository.save(post);
    }
}
