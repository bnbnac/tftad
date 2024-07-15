package com.tftad;

import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.Question;
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
        return Member.builder()
                .email("email")
                .password("password")
                .name("name")
                .build();
    }

    public Channel createChannel(Member member) {
        return Channel.builder()
                .thumbnail("thumbnail")
                .youtubeChannelId("youtubeChannelId")
                .channelTitle("channelTitle")
                .memberId(member.getId())
                .build();
    }

    public Question createQuestion(Post post) {
        return Question.builder()
                .post(post)
                .startTime("000100")
                .endTime("000230")
                .authorIntention("authorIntention")
                .build();
    }

    public Post createPost(Member member, Channel channel) {
        return Post.builder()
                .title("title")
                .content("content")
                .videoId("videoId")
                .memberId(member.getId())
                .channelId(channel.getId())
                .build();
    }

    public void setup() {
        Member member = createMember();
        Channel channel = createChannel(member);
        createPost(member, channel);
    }
}
