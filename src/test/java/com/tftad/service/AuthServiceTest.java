package com.tftad.service;

import com.tftad.domain.Code;
import com.tftad.domain.Member;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.CodeRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.request.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Code code;

    @BeforeEach
    void clean() {
        memberRepository.deleteAll();
        codeRepository.deleteAll();

        code = Code.builder()
                .email("email@email")
                .durationMinutes(20L)
                .code("code")
                .build();
        code.auth();
        codeRepository.save(code);
    }

    @Test
    @DisplayName("회원가입 성공")
    void test1() {
        Signup signup = Signup.builder()
                .email("email@email")
                .name("name")
                .password("password")
                .code("code")
                .build();

        // when
        authService.signup(signup);
        Member member = memberRepository.findAll().iterator().next();

        // then
        assertThat(member.getEmail()).isEqualTo("email@email");
        assertThat(member.getName()).isEqualTo("name");
        assertTrue(passwordEncoder.matches("password", member.getPassword()));
    }

    @Test
    @DisplayName("가입 실패 - 중복된 메일")
    void test2() {
        Signup signup = Signup.builder()
                .email("email@email")
                .name("name")
                .password("password")
                .code("code")
                .build();

        // when
        authService.signup(signup);

        // then
        assertThatThrownBy(() -> {
            authService.signup(signup);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("가입 실패 - 생성되지 않은 코드")
    void test3() {
        Signup signup = Signup.builder()
                .email("email@email")
                .name("name")
                .password("password")
                .code("codeOther")
                .build();

        // when then
        assertThatThrownBy(() -> {
            authService.signup(signup);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("가입 실패 - 만료된 코드")
    void test4() {
        code = Code.builder()
                .email("email@email")
                .durationMinutes(0L) // expired
                .code("code")
                .build();
        code.auth();
        codeRepository.save(code);

        Signup signup = Signup.builder()
                .email("email@email")
                .name("name")
                .password("password")
                .code("code")
                .build();

        // when then
        assertThatThrownBy(() -> {
            authService.signup(signup);
        }).isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("가입 실패 - unAuthed 코드 - 코드 요청 후 인증여부를 가입폼 최종제출시 재확인")
    void test5() {
        code = Code.builder()
                .email("email@email")
                .durationMinutes(20L)
                .code("code")
                .build();
        // code.auth(); // not authed
        codeRepository.save(code);

        Signup signup = Signup.builder()
                .email("email@email")
                .name("name")
                .password("password")
                .code("code")
                .build();

        // when then
        assertThatThrownBy(() -> {
            authService.signup(signup);
        }).isInstanceOf(InvalidRequest.class);
    }


//      check(), login() test
//    @Test
//    @DisplayName("로그인 성공")
//    void test333() {
//        Signup signup = Signup.builder()
//                .email("test3@test.com")
//                .name("loginTest")
//                .password("1234")
//                .build();
//
//        authService.signup(signup);
//
//        Login login = Login.builder()
//                .email("test3@test.com")
//                .password("1234")
//                .build();
//
//        Long memberId = authService.login(login);
//        assertNotNull(memberId);
//
//    }
//
//    @Test
//    @DisplayName("로그인 비밀번호 틀림")
//    void test444() {
//        Signup signup = Signup.builder()
//                .email("test4@test.com")
//                .name("loginTest2")
//                .password("1234")
//                .build();
//
//        authService.signup(signup);
//
//        Login login = Login.builder()
//                .email("test4@test.com")
//                .password("5678")
//                .build();
//
//        assertThrows(InvalidLoginInformation.class, () -> authService.login(login));
//
//    }
}