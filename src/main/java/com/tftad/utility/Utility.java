package com.tftad.utility;

import com.tftad.config.property.JwtProperty;
import com.tftad.exception.Unauthorized;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class Utility {

    private final JwtProperty jwtProperty;

    public String generateJws(JwtBuilder jwtBuilder, int maxAgeInDays) {
        return jwtBuilder
                .expiration(Date.from(Instant.now().plus(maxAgeInDays, ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(jwtProperty.getKey()))
                .compact();
    }

    public ResponseCookie generateCookie(String cookieName, String jws, int maxAgeInDays) {
        return ResponseCookie.from(cookieName, jws)
                .domain(jwtProperty.getDomain())
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(maxAgeInDays))
                .sameSite("strict")
                .build();
    }

    public Cookie[] extractCookiesFromRequest(HttpServletRequest servletRequest) {
        if (servletRequest == null) {
            log.error("servletRequest is null");
            throw new Unauthorized();
        }

        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null || cookies.length == 0) {
            log.error("cookie is null");
            throw new Unauthorized();
        }

        return cookies;
    }

    public String extractValueByCookieName(Cookie[] cookies, String cookieName) {
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new Unauthorized();
    }

    public Jws<Claims> parseJws(String jws) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtProperty.getKey()))
                    .build()
                    .parseSignedClaims(jws);
        } catch (JwtException e) {
            throw new Unauthorized();
        }
    }

    public void verifyExpiration(Claims payload) {
        Long expiration = payload.get(JwtProperty.EXPIRATION, Long.class);
        if (expiration == null) {
            throw new Unauthorized();
        }

        java.util.Date expirationDate = new java.util.Date(expiration * 1000);
        if (expirationDate.before(new java.util.Date())) {
            throw new Unauthorized();
        }
    }
}
