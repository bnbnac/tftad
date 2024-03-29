//package com.tftad.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tftad.config.property.AuthProperty;
//import com.tftad.config.property.JwtProperty;
//import com.tftad.domain.Channel;
//import com.tftad.domain.Member;
//import com.tftad.domain.Post;
//import com.tftad.repository.ChannelRepository;
//import com.tftad.repository.MemberRepository;
//import com.tftad.repository.PostRepository;
//import com.tftad.request.PostCreate;
//import com.tftad.request.PostEdit;
//import com.tftad.service.ChannelService;
//import com.tftad.service.ExtractorService;
//import com.tftad.service.PostService;
//import io.jsonwebtoken.JwtBuilder;
//import io.jsonwebtoken.Jwts;
//import jakarta.servlet.http.Cookie;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static com.tftad.utility.Utility.generateJws;
//import static org.hamcrest.Matchers.is;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//class PostControllerTest {
//    @MockBean
//    private ExtractorService extractorService;
//    @Autowired
//    private PostService postService;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private PostRepository postRepository;
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private ChannelService channelService;
//    @Autowired
//    private ChannelRepository channelRepository;
//    @Autowired
//    private AuthProperty authProperty;
//    @Autowired
//    private JwtProperty jwtProperty;
//
//    private Cookie cookie;
//
//    @BeforeEach
//    void clean() {
//        postRepository.deleteAll();
//        memberRepository.deleteAll();
//        channelRepository.deleteAll();
//        setCookie();
//    }
//
//    void setCookie() {
//        JwtBuilder builder = Jwts.builder()
//                .claim(AuthProperty.MEMBER_ID, "2");
//        String jws = generateJws(builder, jwtProperty.getKey(), jwtProperty.getMaxAgeInDays());
//
//        cookie = new Cookie(authProperty.getTftadCookieName(), jws);
//    }
//
//    @Test
//    @DisplayName("post 작성시 로그인이 필요하다")
//    void test0() throws Exception {
//        PostCreate request = PostCreate.builder()
//                .title("제목")
//                .content("내용")
//                .build();
//
//        String json = objectMapper.writeValueAsString(request);
//
//        mockMvc.perform(post("/posts")
//                                .contentType(APPLICATION_JSON)
//                                .content(json)
//                        //.cookie() empty cookie
//                )
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @DisplayName("post 작성시 post id를 리턴한다")
//    void test1() throws Exception {
//        Member member = Member.builder()
//                .name("hi")
//                .password("hi")
//                .email("hi")
//                .build();
//        memberRepository.save(member);
//
//        Channel channel = Channel.builder()
//                .youtubeChannelId("UCdrwiYsO52W3rrUS_wHW9mA")
//                .channelTitle("title")
//                .member(member)
//                .build();
//        channelRepository.save(channel);
//
//        PostCreate request = PostCreate.builder()
//                .title("제목")
//                .content("내용")
//                .videoUrl("https://youtu.be/j46wk31wTsY")
//                .build();
//        String json = objectMapper.writeValueAsString(request);
//
//        mockMvc.perform(post("/posts")
//                        .contentType(APPLICATION_JSON)
//                        .content(json)
//                        .cookie(cookie)
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string("1"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("post 작성시 title 값은 필수다")
//    void test2_1() throws Exception {
//        PostCreate request = PostCreate.builder()
//                .content("내용")
//                .videoUrl("url")
//                .build();
//
//        mockMvc.perform(post("/posts")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                        .cookie(cookie)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("400"))
//                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
//                .andExpect(jsonPath("$.validations[0].field").value("title"))
//                .andExpect(jsonPath("$.validations[0].message").value("타이틀을 입력해주세요"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("post 작성시 content 값은 필수다")
//    void test2_2() throws Exception {
//        PostCreate request = PostCreate.builder()
//                .title("제목")
//                .videoUrl("url")
//                .build();
//
//        mockMvc.perform(post("/posts")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                        .cookie(cookie)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("400"))
//                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
//                .andExpect(jsonPath("$.validations[0].field").value("content"))
//                .andExpect(jsonPath("$.validations[0].message").value("콘텐츠를 입력해주세요"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("post 작성시 DB에 값이 저장된다")
//    void test3() throws Exception {
//        PostCreate request = PostCreate.builder()
//                .title("제목입니다")
//                .content("내용입니다")
//                .videoUrl("url")
//                .build();
//        String json = objectMapper.writeValueAsString(request);
//
//        mockMvc.perform(post("/posts")
//                        .contentType(APPLICATION_JSON)
//                        .content(json)
//                        .cookie(cookie)
//                )
//                .andExpect(status().isOk())
//                .andDo(print());
//
//        assertEquals(1L, postRepository.count());
//
//        Post post = postRepository.findAll().iterator().next();
//        assertEquals("제목입니다", post.getTitle());
//        assertEquals("내용입니다", post.getContent());
//    }
//
//    @Test
//    @DisplayName("post 1개 조회")
//    void test4() throws Exception {
//        Post post = Post.builder()
//                .title("123451234512345")
//                .content("내용")
//                .build();
//        postRepository.save(post);
//
//        mockMvc.perform(get("/posts/{postId}", post.getId())
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(post.getId()))
//                .andExpect(jsonPath("$.title").value("1234512345"))
//                .andExpect(jsonPath("$.content").value("내용"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("post 여러개 조회")
//    void test5() throws Exception {
//        List<Post> requestPosts = IntStream.range(0, 30)
//                .mapToObj(i -> Post.builder()
//                        .title("제목" + i)
//                        .content("내용" + i)
//                        .build())
//                .collect(Collectors.toList());
//        postRepository.saveAll(requestPosts);
//
//        mockMvc.perform(get("/posts?page=1&size=10")
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()", is(10)))
//                .andExpect(jsonPath("$[0].id").value(requestPosts.get(29).getId()))
//                .andExpect(jsonPath("$[0].title").value("제목29"))
//                .andExpect(jsonPath("$[0].content").value("내용29"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("페이지를 0으로 요청하면 첫 페이지를 가져온다")
//    void test6() throws Exception {
//        List<Post> requestPosts = IntStream.range(0, 30)
//                .mapToObj(i -> Post.builder()
//                        .title("제목" + i)
//                        .content("내용" + i)
//                        .build())
//                .collect(Collectors.toList());
//        postRepository.saveAll(requestPosts);
//
//        mockMvc.perform(get("/posts?page=0&size=10")
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()", is(10)))
//                .andExpect(jsonPath("$[0].id").value(requestPosts.get(29).getId()))
//                .andExpect(jsonPath("$[0].title").value("제목29"))
//                .andExpect(jsonPath("$[0].content").value("내용29"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("post 제목 수정")
//    void test7() throws Exception {
//        Post post = Post.builder()
//                .title("제목")
//                .content("내용")
//                .build();
//        postRepository.save(post);
//
//        PostEdit postEdit = PostEdit.builder()
//                .title("수정제목")
//                .build();
//
//        mockMvc.perform(patch("/posts/{postId}", post.getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(postEdit))
//                )
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("post 삭제")
//    void test8() throws Exception {
//        Post post = Post.builder()
//                .title("제목")
//                .content("내용")
//                .build();
//        postRepository.save(post);
//
//        mockMvc.perform(delete("/posts/{postId}", post.getId())
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글 조회")
//    void test9() throws Exception {
//        mockMvc.perform(get("/posts/{postId}", 1L)
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글 수정")
//    void test10() throws Exception {
//        PostEdit postEdit = PostEdit.builder()
//                .title("수정제목")
//                .build();
//
//        mockMvc.perform(patch("/posts/{postId}", 1L)
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(postEdit))
//                )
//                .andExpect(status().isNotFound())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글 삭제")
//    void test11() throws Exception {
//        mockMvc.perform(delete("/posts/{postId}", 1L)
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isNotFound())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("작성 게시글 제목에 '바보'는 포함될 수 없다")
//    void test12() throws Exception {
//        PostCreate request = PostCreate.builder()
//                .title("나는 바보다")
//                .content("내용")
//                .videoUrl("url")
//                .build();
//
//        mockMvc.perform(post("/posts")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//                        .cookie(cookie)
//                )
//                .andExpect(status().isBadRequest())
//                .andDo(print());
//    }
//}