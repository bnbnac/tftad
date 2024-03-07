package com.tftad.utility;

import com.tftad.config.property.AuthProperty;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.Unauthorized;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Utility {
    public static String generateJws(JwtBuilder jwtBuilder, byte[] jwtByteKey, int maxAgeInDays) {
        return jwtBuilder
                .expiration(Date.from(Instant.now().plus(maxAgeInDays, ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(jwtByteKey))
                .compact();
    }

    public static ResponseCookie generateCookie(String domain, String cookieName, String jws, int maxAgeInDays) {
        return ResponseCookie.from(cookieName, jws)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(maxAgeInDays))
                .sameSite("strict")
                .build();
    }

    public static Cookie[] extractCookiesFromRequest(HttpServletRequest servletRequest) {
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

    public static String extractValueByCookieName(Cookie[] cookies, String cookieName) {
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new Unauthorized();
    }

    public static Jws<Claims> parseJws(String jws, byte[] jwtByteKey) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtByteKey))
                    .build()
                    .parseSignedClaims(jws);
        } catch (JwtException e) {
            throw new Unauthorized();
        }
    }

    public static void verifyExpiration(Claims payload) {
        Long expiration = payload.get(AuthProperty.EXPIRATION, Long.class);
        if (expiration == null) {
            throw new Unauthorized();
        }

        java.util.Date expirationDate = new java.util.Date(expiration * 1000);
        if (expirationDate.before(new java.util.Date())) {
            throw new Unauthorized();
        }
    }

    public static String extractVideoId(String url) {
        validateVideoUrl(url);
        return applyRegexToExtractVideoId(url);
    }

    public static void validateVideoUrl(String url) {
        if (!url.contains("youtu")) {
            throw new InvalidRequest("url", "올바른 유튜브 영상 주소를 입력해주세요");
        }
    }

    public static String applyRegexToExtractVideoId(String url) {
        String regex = "(?<=(v%3D-|v=|v/|youtu.be/))[\\w-]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        }
        throw new InvalidRequest("url", "올바른 유튜브 영상 주소를 입력해주세요");
    }
}
