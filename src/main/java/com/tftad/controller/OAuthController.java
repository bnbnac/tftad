package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.service.OAuthService;
import com.tftad.util.JwtUtility;
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

    private final OAuthService oAuthService;
    private final JwtUtility jwtUtility;
    private final JwtProperty jwtProperty;
    private final GoogleOAuthProperty googleOAuthProperty;

    @GetMapping("/oauth/login/google")
    public ResponseEntity<Object> getGoogleOAuthRedirection(AuthenticatedMember member, @RequestParam String code) {
        long memberId = member.getId();

        JwtBuilder builder = Jwts.builder()
                .claim(jwtProperty.MEMBER_ID, String.valueOf(memberId))
                .claim(googleOAuthProperty.AUTHORIZATION_CODE, code);
        String jws = jwtUtility.generateJws(builder, googleOAuthProperty.getCookieMaxAgeInDays());
        ResponseCookie cookie = jwtUtility.generateCookie(
                jwtProperty.getCookieName(), jws, googleOAuthProperty.getCookieMaxAgeInDays());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/oauth/add/channel")
    public void addYoutubeChannel(AuthenticatedMember member) {
        String channelId = oAuthService.getChannelId(member.getAuthorizationCode());

        System.out.println("memberId: " + member.getId());
        System.out.println("code: " + member.getAuthorizationCode());

        // todo
        System.out.println("add this channelId to member entity: " + channelId);
    }
}
