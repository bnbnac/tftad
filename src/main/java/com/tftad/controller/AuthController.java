package com.tftad.controller;

import com.tftad.config.AppConfig;
import com.tftad.config.data.MemberSession;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import com.tftad.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AppConfig appConfig;

    @GetMapping("/foo")
    public Long foo(MemberSession memberSession) {
        return memberSession.id;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody Login login) {

        Long memberId = authService.login(login);

        String jws = Jwts.builder()
                .claim("member_id", String.valueOf(memberId))
                .expiration(Date.from(Instant.now().plusSeconds(604800)))
                .signWith(Keys.hmacShaKeyFor(appConfig.getKey()))
                .compact();

        ResponseCookie cookie = ResponseCookie.from("ML", jws)
                .domain("localhost") // Todo 서버 환경에 따른 분리 필요
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(30))
                .sameSite("strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/auth/signup")
    public void signup(@RequestBody Signup signup) {
        authService.signup(signup);
    }

}
