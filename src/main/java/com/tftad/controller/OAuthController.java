package com.tftad.controller;

import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.config.property.JwtProperty;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.tftad.utility.Utility.generateCookie;
import static com.tftad.utility.Utility.generateJws;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final GoogleOAuthProperty googleOAuthProperty;
    private final JwtProperty jwtProperty;

    @GetMapping("/oauth/login/google")
    public ResponseEntity<Object> getGoogleOAuthRedirection(@RequestParam String code) {

        JwtBuilder builder = Jwts.builder()
                .claim(GoogleOAuthProperty.AUTHORIZATION_CODE, code);
        String jws = generateJws(builder, jwtProperty.getKey(), googleOAuthProperty.getCookieMaxAgeInDays());

        ResponseCookie cookie = generateCookie(
                jwtProperty.getDomain(), googleOAuthProperty.getCookieName(), jws, googleOAuthProperty.getCookieMaxAgeInDays());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
