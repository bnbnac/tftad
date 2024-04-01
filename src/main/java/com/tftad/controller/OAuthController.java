package com.tftad.controller;

import com.tftad.config.property.AuthProperty;
import com.tftad.config.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final JwtProperty jwtProperty;
    private final AuthProperty authProperty;

//    @GetMapping("/oauth/login/google") // injection?
//    public ResponseEntity<Object> getGoogleOAuthRedirection(@RequestParam String code) {
//
//        JwtBuilder builder = Jwts.builder()
//                .claim(AuthProperty.AUTHORIZATION_CODE, code);
//        String jws = generateJws(builder, jwtProperty.getKey(), jwtProperty.getMaxAgeInDays());
//
//        ResponseCookie cookie = generateCookie(
//                authProperty.getTftadDomain(),
//                authProperty.getGoogleCookieName(),
//                jws,
//                authProperty.getGoogleCookieMaxAgeInDays());
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .build();
//    }
}
