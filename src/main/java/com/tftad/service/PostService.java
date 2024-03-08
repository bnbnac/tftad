package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.PostEditor;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.PostRepository;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Long savePost(Member member, PostCreate postCreate) {
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

    public Post validateCompletedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new InvalidRequest("postId", "존재하지 않는 게시글입니다"));

        if (post.getPublished()) {
            throw new InvalidRequest("postId", "이미 발행된 게시글입니다");
        }

        return post;
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFound::new);
    }

    public void showPost(Post post) {
        post.show();
        postRepository.save(post);
    }

    public void validatePostedVideo(String videoId) {
        postRepository.findByVideoId(videoId)
                .ifPresent(p -> {
                    throw new InvalidRequest(
                            "videoId", "이미 등록된 영상입니다. postId: " + p.getId()
                    );
                });
    }

    public Post validatePostBeforeGetPosition(Long memberId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        if (!memberId.equals(post.getMember().getId())) {
            throw new InvalidRequest("postId", "게시글의 작성자만 작업상황을 조회할 수 있습니다");
        }
        return post;
    }
}
