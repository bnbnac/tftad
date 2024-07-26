package com.tftad.utility;

import com.tftad.config.property.AuthProperty;
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
@RequiredArgsConstructor
@Component
public class JwtUtils {

    private final JwtProperty jwtProperty;
    private final AuthProperty authProperty;

    public String generateJws(JwtBuilder jwtBuilder, Long maxAgeInMinutes) {
        return jwtBuilder
                .expiration(Date.from(Instant.now().plus(maxAgeInMinutes, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(jwtProperty.getKey()))
                .compact();
    }

    public ResponseCookie createCookie(String cookieName, String cookieValue, Long maxAge) {
        return ResponseCookie.from(cookieName, cookieValue)
                .domain(authProperty.getTftadDomain())
                .path("/")
                .httpOnly(true)
                .secure(jwtProperty.isCookieSecure())
                .maxAge(Duration.ofDays(maxAge))
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

    public void validateExpiration(Claims payload) {
        Long expiration = payload.get(AuthProperty.EXPIRATION, Long.class);
        if (expiration == null) {
            throw new Unauthorized();
        }

        Instant expirationDate = Instant.ofEpochSecond(expiration);

        if (expirationDate.isBefore(Instant.now())) {
            throw new Unauthorized();
        }
    }
}
