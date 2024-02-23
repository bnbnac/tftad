package com.tftad.controller;

import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.utility.Utility;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final Utility utility;
    private final GoogleOAuthProperty googleOAuthProperty;

    @GetMapping("/oauth/login/google")
    public ResponseEntity<Object> getGoogleOAuthRedirection(@RequestParam String code) {

        JwtBuilder builder = Jwts.builder()
                .claim(GoogleOAuthProperty.AUTHORIZATION_CODE, code);
        String jws = utility.generateJws(builder, googleOAuthProperty.getCookieMaxAgeInDays());

        ResponseCookie cookie = utility.generateCookie(
                googleOAuthProperty.getCookieName(), jws, googleOAuthProperty.getCookieMaxAgeInDays());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

}
