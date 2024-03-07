package com.tftad.service;

import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.PostEditor;
import com.tftad.exception.ChannelNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    private final OAuthService oAuthService;
    private final ExtractorService extractorService;

    @Transactional
    public Long write(Long memberId, PostCreate postCreate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFound::new);

        validateChannelOwner(member, postCreate.getVideoId());
        validatePostedVideo(member, postCreate.getVideoId());
        Long postId = savePost(member, postCreate);

        extractorService.queryAnalysis(postCreate.getVideoId(), postId);

        return postId;
    }

    private void validateChannelOwner(Member member, String videoId) {
        String youtubeChannelId = oAuthService.queryVideoResourceToGetChannelId(videoId);
        Channel channel = channelRepository.findByYoutubeChannelId(youtubeChannelId)
                .orElseThrow(ChannelNotFound::new);

        if (!member.getId().equals(channel.getMember().getId())) {
            throw new InvalidRequest("url", "계정에 유튜브 채널을 등록해주세요");
        }
    }

    private void validatePostedVideo(Member member, String videoId) {
        List<Post> posts = member.getPosts();
        for (Post post : posts) {
            if (post.getVideoId().equals(videoId)) {
                throw new InvalidRequest("videoId", "이미 등록된 영상입니다");
            }
        }
    }

    private Long savePost(Member member, PostCreate postCreate) {
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .videoId(postCreate.getVideoId())
                .member(member)
                .build();
        return postRepository.save(post).getId();
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    public PostResponse edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        PostEditor postEditor = post.toEditorBuilder()
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
        postRepository.save(post);

        return new PostResponse(post);
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        postRepository.delete(post);
    }
}
