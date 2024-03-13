package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.domain.Post;
import com.tftad.domain.PostCreateDto;
import com.tftad.domain.PostEditDto;
import com.tftad.exception.ExtractorServerError;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.ChannelRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.repository.PostRepository;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();
    }

    Member generateMember(String name, String password, String email) {
        return Member.builder()
                .name(name)
                .password(password)
                .email(email)
                .build();
    }

    Post generatePost(String content, Member member, String videoId, String title) {
        return Post.builder()
                .content(content)
                .member(member)
                .videoId(videoId)
                .title(title)
                .build();
    }

    @Test
    @DisplayName("post 저장")
    void test1() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long id = memberRepository.save(member).getId();

        PostCreateDto postCreateDto = PostCreateDto.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .memberId(id)
                .build();

        // when
        postService.savePost(postCreateDto);

        // then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().iterator().next();
        assertEquals("제목", post.getTitle());
        assertEquals("내용", post.getContent());
        assertEquals("videoId", post.getVideoId());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when
        PostResponse response = postService.get(post.getId());

        // then
        assertNotNull(response);
        assertEquals("제목", response.getTitle());
        assertEquals("내용", response.getContent());
        assertEquals("videoId", response.getVideoId());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void test3() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> Post.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .videoId("videoId" + i)
                        .member(member)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
                .build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertEquals(10L, posts.size());
        assertEquals("제목29", posts.get(0).getTitle());
        assertEquals("제목20", posts.get(9).getTitle());
    }

    @Test
    @DisplayName("글 제목만 수정")
    void test4() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        PostEditDto postEditDto = PostEditDto.builder()
                .title("수정제목")
                .content(null)
                .postId(postId)
                .memberId(memberId)
                .build();

        // when
        postService.edit(postEditDto);
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));

        // then
        assertEquals("수정제목", changedPost.getTitle());
        assertEquals("내용", changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test5() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        PostEditDto postEditDto = PostEditDto.builder()
                .title(null)
                .content("수정내용")
                .postId(postId)
                .memberId(memberId)
                .build();

        // when
        postService.edit(postEditDto);
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));


        // then
        assertEquals("제목", changedPost.getTitle());
        assertEquals("수정내용", changedPost.getContent());
    }

    @Test
    @DisplayName("글 삭제")
    void test6() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when
        postService.delete(member.getId(), post.getId());


        //then
        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test7() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when then
        assertThrows(PostNotFound.class, () -> {
            postService.get(post.getId() + 1);
        });
    }

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void test8_1() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId = memberRepository.save(member).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        Long postId = postRepository.save(post).getId();

        // when
        PostEditDto postEditDto = PostEditDto.builder()
                .postId(postId + 1).memberId(memberId).content("c").title("t").build();

        // then
        assertThrows(PostNotFound.class, () -> {
            postService.edit(postEditDto);
        });
    }


    @Test
    @DisplayName("글 내용 수정 - 작성자가 아닌 멤버")
    void test8_2() {
        Member member1 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId1 = memberRepository.save(member1).getId();

        Member member2 = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        Long memberId2 = memberRepository.save(member2).getId();

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member1)
                .build();
        Long postId = postRepository.save(post).getId();

        // when
        PostEditDto postEditDto = PostEditDto.builder()
                .postId(postId).memberId(memberId2).content("c").title("t").build();

        // then
        assertThrows(InvalidRequest.class, () -> {
            postService.edit(postEditDto);
        });
    }

    @Test
    @DisplayName("글 삭제 - 존재하지 않는 글")
    void test9() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when then
        assertThrows(PostNotFound.class, () -> {
            postService.delete(member.getId(), post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("showPost 테스트")
    void test10() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post1 = Post.builder()
                .title("제목1")
                .content("내용1")
                .videoId("videoId1")
                .member(member)
                .build();
        Post post2 = Post.builder()
                .title("제목2")
                .content("내용2")
                .videoId("videoId2")
                .member(member)
                .build();
        postRepository.save(post1);
        postRepository.save(post2);

        // when
        postService.showPost(post1.getId());

        Iterator<Post> iterator = postRepository.findAll().iterator();
        Post foundPost1 = iterator.next();
        Post foundPost2 = iterator.next();

        // then
        assertTrue(foundPost1.getPublished());
        assertFalse(foundPost2.getPublished());
    }

    @Test
    @DisplayName("validatePublishedPost 테스트: publish 된 post")
    void test12() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when
        postService.showPost(post.getId());

        // then
        assertThrows(InvalidRequest.class, () -> {
            postService.validatePublishedPost(post.getId());
        });
    }

    @Test
    @DisplayName("validatePublishedPost 테스트: 존재하지 않는 post")
    void test13() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();

        // when
        postRepository.save(post);

        // then
        assertThrows(InvalidRequest.class, () -> {
            postService.validatePublishedPost(post.getId() + 1); //
        });
    }

    @Test
    @DisplayName("validatePublishedPost 테스트: unpublished")
    void test14() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when then
        assertDoesNotThrow(() -> postService.validatePublishedPost(post.getId()));

    }

    @Test
    @DisplayName("validatePostedVideo 테스트: 등록되지 않은 비디오")
    void test15() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);
        postService.showPost(post.getId());

        PostCreateDto postCreateDto = PostCreateDto.builder()
                .videoId(post.getVideoId() + "other")
                .memberId(post.getMember().getId())
                .content(post.getContent())
                .title(post.getTitle())
                .build();

        // when then
        assertDoesNotThrow(() -> postService.validatePostedVideo(postCreateDto));
    }

    @Test
    @DisplayName("validatePostedVideo 테스트: 등록된 비디오")
    void test16() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        PostCreateDto postCreateDto = PostCreateDto.builder()
                .videoId(post.getVideoId())
                .memberId(post.getMember().getId())
                .content(post.getContent())
                .title(post.getTitle())
                .build();

        // when then
        assertThrows(InvalidRequest.class, () -> postService.validatePostedVideo(postCreateDto));
    }

    @Test
    @DisplayName("validateToGetPosition 테스트: 본인 게시글인 경우")
    void test17() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when then
        assertDoesNotThrow(() -> postService.validateToGetPosition(member.getId(), post.getId()));
    }

    @Test
    @DisplayName("validateToGetPosition 테스트: 본인 게시글이 아닌 경우")
    void test18() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        // when then
        assertThrows(InvalidRequest.class, () -> {
            postService.validateToGetPosition(member.getId() + 1L, post.getId());
        });
    }

    @Test
    @DisplayName("validateExtractorResultOrDeletePost 테스트: 정상")
    void test19() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        List<String> result = List.of("1", "2", "3", "4");

        // when
        assertDoesNotThrow(() -> postService.validateExtractorResultOrDeletePost(post.getId(), result));

        // then
        assertEquals(1L, postRepository.count());
    }

    @Test
    @DisplayName("validateExtractorResultOrDeletePost 테스트: 홀수 result")
    void test20() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        List<String> result = List.of("1", "2", "3");

        // when
        assertThrows(ExtractorServerError.class, () -> {
            postService.validateExtractorResultOrDeletePost(post.getId(), result);
        });

        // then
        assertEquals(0L, postRepository.count());
    }

    @Test
    @DisplayName("validateExtractorResultOrDeletePost 테스트: empty result")
    void test21() {
        Member member = Member.builder()
                .email("email")
                .password("pswd")
                .name("name")
                .build();
        memberRepository.save(member);

        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .videoId("videoId")
                .member(member)
                .build();
        postRepository.save(post);

        List<String> result = List.of();

        // when
        assertThrows(ExtractorServerError.class, () -> {
            postService.validateExtractorResultOrDeletePost(post.getId(), result);
        });

        // then
        assertEquals(0L, postRepository.count());
    }
}