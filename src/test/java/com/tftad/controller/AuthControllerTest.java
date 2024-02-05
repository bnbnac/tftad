package com.tftad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tftad.repository.MemberRepository;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.support.TransactionTemplate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void clean() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 성공")
    void test1() throws Exception {
        Signup signup = Signup.builder()
                .name("hi")
                .email("test1@test.com")
                .password("1234")
                .build();
        mockMvc.perform(post("/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup))
        );

        Login login = Login.builder()
                .email("test1@test.com")
                .password("1234")
                .build();
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

//    ***   test for session db auth    ***
//
//    @Test
//    @DisplayName("로그인 성공 후 세션 생성")
//    void test2() throws Exception {
//        Member member = memberRepository.save(Member.builder()
//                .name("hi")
//                .email("test2@test.com")
//                .password("1234")
//                .build());
//
//        Login login = Login.builder()
//                .email("test2@test.com")
//                .password("1234")
//                .build();
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(login))
//                )
//                .andExpect(status().isOk())
//                .andDo(print());
//
//        List<Session> memberSessions = transactionTemplate.execute(status -> {
//            List<Session> sessions = memberRepository.findById(member.getId())
//                    .orElseThrow(UserNotFound::new)
//                    .getSessions();
//            Hibernate.initialize(sessions);
//            return sessions;
//        });
//
//        Assertions.assertEquals(1L, memberSessions.size());
//    }

    @Test
    @DisplayName("로그인 성공 후 쿠키 응답")
    void test3() throws Exception {
        Signup signup = Signup.builder()
                .name("hi")
                .email("test3@test.com")
                .password("1234")
                .build();
        mockMvc.perform(post("/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup))
        );

        Login login = Login.builder()
                .email("test3@test.com")
                .password("1234")
                .build();
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                .andExpect(status().isOk())
                .andExpect(header().exists("set-cookie"))
                .andDo(print());
    }

//    ***   test for session db auth    ***
//
//    @Test
//    @DisplayName("로그인 후 권한이 필요한 페이지에 접속한다")
//    void test4() throws Exception {
//        Member member = Member.builder()
//                .name("hi")
//                .email("test4@test.com")
//                .password("1234")
//                .build();
//        Session session = member.addSession();
//
//        memberRepository.save(member);
//
//        mockMvc.perform(get("/foo")
//                        .cookie(new Cookie("TFTAD_SESSION", session.getAccessToken()))
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("검증되지 않은 세션값으로 권한이 필요한 페이지에 접속할 수 없다")
//    void test5() throws Exception {
//        Member member = Member.builder()
//                .name("hi")
//                .email("test5@test.com")
//                .password("1234")
//                .build();
//        Session session = member.addSession();
//
//        memberRepository.save(member);
//
//        mockMvc.perform(get("/foo")
//                        .cookie(new Cookie("TFTAD_SESSION", session.getAccessToken() + "-other"))
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isUnauthorized())
//                .andDo(print());
//    }

    @Test
    @DisplayName("로그인 후 권한이 필요한 페이지에 접속한다")
    void test4() throws Exception {
        Signup signup = Signup.builder()
                .name("hi")
                .email("test4@test.com")
                .password("1234")
                .build();
        ResultActions resultActions = mockMvc.perform(post("/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup))
        );


        Login login = Login.builder()
                .email("test4@test.com")
                .password("1234")
                .build();
        String cookie = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                .andReturn()
                .getResponse()
                .getCookie("ML")
                .getValue();

        mockMvc.perform(get("/foo")
                        .cookie(new Cookie("ML", cookie))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("검증되지 않은 세션값으로 권한이 필요한 페이지에 접속할 수 없다")
    void test5() throws Exception {
        Signup signup = Signup.builder()
                .name("hi")
                .email("test4@test.com")
                .password("1234")
                .build();
        ResultActions resultActions = mockMvc.perform(post("/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup))
        );


        Login login = Login.builder()
                .email("test4@test.com")
                .password("1234")
                .build();
        String cookie = mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login))
                )
                .andReturn()
                .getResponse()
                .getCookie("ML")
                .getValue();

        mockMvc.perform(get("/foo")
                        .cookie(new Cookie("ML", cookie + "other"))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입")
    void test6() throws Exception {
        Signup signup = Signup.builder()
                .email("test6@test.com")
                .name("signupTest")
                .password("1234")
                .build();

        mockMvc.perform(post("/auth/signup")
                        .content(objectMapper.writeValueAsString(signup))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}