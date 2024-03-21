package com.tftad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tftad.config.property.AuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.repository.MemberRepository;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private JwtProperty jwtProperty;

    @Autowired
    private AuthProperty authProperty;

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
//
//    @Test
//    @DisplayName("로그인 후 권한이 필요한 페이지에 접속한다")
//    void test4() throws Exception {
//        Signup signup = Signup.builder()
//                .name("hi")
//                .email("test4@test.com")
//                .password("1234")
//                .build();
//        ResultActions resultActions = mockMvc.perform(post("/auth/signup")
//                .contentType(APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(signup))
//        );
//
//        Login login = Login.builder()
//                .email("test4@test.com")
//                .password("1234")
//                .build();
//        String cookie = mockMvc.perform(post("/auth/login")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(login))
//                )
//                .andReturn()
//                .getResponse()
//                .getCookie(authProperty.getTftadCookieName())
//                .getValue();
//
//        mockMvc.perform(get("/foo")
//                        .cookie(new Cookie(authProperty.getTftadCookieName(), cookie))
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("검증되지 않은 인증정보로 권한이 필요한 페이지에 접속할 수 없다")
//    void test5() throws Exception {
//        Signup signup = Signup.builder()
//                .name("hi")
//                .email("test4@test.com")
//                .password("1234")
//                .build();
//        ResultActions resultActions = mockMvc.perform(post("/auth/signup")
//                .contentType(APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(signup))
//        );
//
//        Login login = Login.builder()
//                .email("test4@test.com")
//                .password("1234")
//                .build();
//        String cookie = mockMvc.perform(post("/auth/login")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(login))
//                )
//                .andReturn()
//                .getResponse()
//                .getCookie(authProperty.getTftadCookieName())
//                .getValue();
//
//        mockMvc.perform(get("/foo")
//                        .cookie(new Cookie(authProperty.getTftadCookieName(), cookie + "other"))
//                        .contentType(APPLICATION_JSON)
//                )
//                .andExpect(status().isUnauthorized())
//                .andDo(print());
//    }

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