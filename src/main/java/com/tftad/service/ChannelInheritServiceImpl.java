package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChannelInheritServiceImpl implements ChannelInheritService {

    private final PostRepository postRepository;

    @Override
    public void inheritPostsOfChannel(Long channelId, Long memberId) {
        List<Post> posts = postRepository.findByChannelId(channelId);
        for (Post post : posts) {
            post.inherit(memberId);
        }
    }
}
