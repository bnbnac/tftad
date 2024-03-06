package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Channel;
import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.PostEditor;
import com.tftad.exception.*;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.request.external.Analysis;
import com.tftad.response.PositionOfPostResponse;
import com.tftad.response.PostResponse;
import com.tftad.utility.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final Utility utility;

    public Long write(AuthenticatedMember authenticatedMember, PostCreate postCreate) {
        String videoId = utility.extractVideoId(postCreate.getVideoUrl());
        Member member = memberRepository.findById(authenticatedMember.getId())
                .orElseThrow(MemberNotFound::new);

        validateChannelOwner(member, videoId);
        validatePostedVideo(member, videoId);

        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .videoId(videoId)
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        queryAnalysis(videoId, postId);
        return postId;
    }

    private void validateChannelOwner(Member member, String videoId) {
        String channelId = oAuthService.queryVideoResourceToGetChannelId(videoId);
        Channel channel = channelRepository.findByYoutubeChannelId(channelId)
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

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    private void queryAnalysis(String videoId, Long postId) {
        Analysis analysis = Analysis.builder()
                .videoId(videoId)
                .postId(postId)
                .build();

        WebClient client = WebClient.create();
        boolean ok = client.post()
                .uri("http://localhost:5050/analysis")
                .bodyValue(analysis)
                .retrieve()
                .toBodilessEntity()
                .block()
                .getStatusCode()
                .is2xxSuccessful();
        if (!ok) {
            throw new ExtractorServerError();
        }
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

    private void validatePostAuthorIsVideoOwner(AuthenticatedMember authenticatedMember, String videoId) {
        String channelId = oAuthService.queryVideoResourceToGetChannelId(videoId);

        Member member = memberRepository.findById(authenticatedMember.getId())
                .orElseThrow(MemberNotFound::new);
        Channel channel = channelRepository.findByYoutubeChannelId(channelId)
                .orElseThrow(ChannelNotFound::new);

        if (!member.getId().equals(channel.getMember().getId())) {
            throw new InvalidRequest("url", "계정에 유튜브 채널을 등록해주세요");
        }
    }

    private boolean queryAnalysis(String videoId, Long postId) {
        Analyze analyze = Analyze.builder()
                .videoId(videoId)
                .postId(postId)
                .build();

        WebClient client = WebClient.create();
        return client.post()
                .uri("localhost:5050/analyze")
                .body(BodyInserters.fromValue(analyze))
                .retrieve()
                .toBodilessEntity()
                .block()
                .getStatusCode()
                .is2xxSuccessful();
    }

}
