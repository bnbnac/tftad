package com.tftad.controller;

import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.service.OAuthService;
import com.tftad.utility.Utility;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final Utility utility;
    private final GoogleOAuthProperty googleOAuthProperty;
    private final OAuthService oAuthService;

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

    @GetMapping("/oauth/channel/{url}")
    public String getChannelIdFromYoutubeUrl(@PathVariable String url) {
        return oAuthService.queryVideoResource(url);
    }
}
