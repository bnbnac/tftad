package com.tftad.controller;

import com.tftad.config.property.AuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import com.tftad.service.AuthService;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.tftad.utility.Utility.generateCookie;
import static com.tftad.utility.Utility.generateJws;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperty jwtProperty;
    private final AuthProperty authProperty;

    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody Login login) {
        Long memberId = authService.login(login);

        JwtBuilder builder = Jwts.builder()
                .claim(AuthProperty.MEMBER_ID, String.valueOf(memberId));
        String jws = generateJws(builder, jwtProperty.getKey(), jwtProperty.getMaxAgeInDays());

        ResponseCookie cookie = generateCookie(
                authProperty.getTftadDomain(),
                authProperty.getTftadCookieName(),
                jws,
                authProperty.getTftadCookieMaxAgeInDays());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/auth/signup")
    public Long signup(@RequestBody Signup signup) {
        return authService.signup(signup);
    }

}
