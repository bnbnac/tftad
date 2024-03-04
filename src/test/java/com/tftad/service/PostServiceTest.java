package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Post;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.PostRepository;
import com.tftad.request.PostCreate;
import com.tftad.request.PostEdit;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1() {
        PostCreate postCreate = PostCreate.builder()
                .title("제목")
                .content("내용")
                .content("url")
                .build();

        postService.write(AuthenticatedMember.builder().build(), postCreate);

        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().iterator().next();
        assertEquals("제목", post.getTitle());
        assertEquals("내용", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        Post requestPost = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(requestPost);

        PostResponse response = postService.get(requestPost.getId());

        assertNotNull(response);
        assertEquals("제목", response.getTitle());
        assertEquals("내용", response.getContent());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test3() {
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .build();

        List<PostResponse> posts = postService.getList(postSearch);

        assertEquals(10L, posts.size());
        assertEquals("제목29", posts.get(0).getTitle());
        assertEquals("제목20", posts.get(9).getTitle());
    }

    @Test
    @DisplayName("글 제목만 수정")
    void test4() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("수정제목")
                .content(null)
                .build();

        postService.edit(post.getId(), postEdit);

        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("수정제목", changedPost.getTitle());
        assertEquals("내용", changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title(null)
                .content("수정내용")
                .build();

        postService.edit(post.getId(), postEdit);

        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        assertEquals("제목", changedPost.getTitle());
        assertEquals("수정내용", changedPost.getContent());
    }

    @Test
    @DisplayName("글 삭제")
    void test6() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        postService.delete(post.getId());

        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test7() {
        Post requestPost = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(requestPost);

        assertThrows(PostNotFound.class, () -> {
            postService.get(requestPost.getId() + 1L);
        });
    }

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void test8() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title(null)
                .content("수정내용")
                .build();

        assertThrows(PostNotFound.class, () -> {
            postService.edit(post.getId() + 1L, postEdit);
        });
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void test9() {
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .build();
        postRepository.save(post);

        assertThrows(PostNotFound.class, () -> {
            postService.delete(post.getId() + 1L);
        });
    }
}