package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import com.tftad.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/foo")
    public Long foo(AuthenticatedMember authenticatedMember) {
        return authenticatedMember.id;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody Login login) {

        Long memberId = authService.login(login);
        String jws = authService.generateJws(memberId);
        ResponseCookie cookie = authService.generateJwtCookie(jws);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/auth/signup")
    public void signup(@RequestBody Signup signup) {
        authService.signup(signup);
    }

}
