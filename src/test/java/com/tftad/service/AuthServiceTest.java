package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.exception.InvalidLoginInformation;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.MemberRepository;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void test1() {
        Signup signup = Signup.builder()
                .email("test1@test.com")
                .name("signupTest")
                .password("1234")
                .build();

        authService.signup(signup);
        assertEquals(1, memberRepository.count());

        Member member = memberRepository.findAll().iterator().next();
        assertEquals("test1@test.com", member.getEmail());
        assertEquals("signupTest", member.getName());
        assertTrue(passwordEncoder.matches("1234", member.getPassword()));
    }

    @Test
    @DisplayName("회원가입시 중복된 메일이 있으면 실패한다")
    void test2() {
        Member member = Member.builder()
                .email("test2@test.com")
                .name("dupTest")
                .password("1234")
                .build();
        memberRepository.save(member);

        Signup signup = Signup.builder()
                .email("test2@test.com")
                .name("signupTest")
                .password("123456")
                .build();

        assertThrows(InvalidRequest.class, () -> {
            authService.signup(signup);
        });
    }

    @Test
    @DisplayName("로그인 성공")
    void test3() {
        Signup signup = Signup.builder()
                .email("test3@test.com")
                .name("loginTest")
                .password("1234")
                .build();

        authService.signup(signup);

        Login login = Login.builder()
                .email("test3@test.com")
                .password("1234")
                .build();

        Long memberId = authService.login(login);
        assertNotNull(memberId);

    }

    @Test
    @DisplayName("로그인 비밀번호 틀림")
    void test4() {
        Signup signup = Signup.builder()
                .email("test4@test.com")
                .name("loginTest2")
                .password("1234")
                .build();

        authService.signup(signup);

        Login login = Login.builder()
                .email("test4@test.com")
                .password("5678")
                .build();

        assertThrows(InvalidLoginInformation.class, () -> authService.login(login));

    }
}