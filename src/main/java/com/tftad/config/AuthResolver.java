package com.tftad.config;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.exception.Unauthorized;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Date;


@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final JwtProperty jwtProperty;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthenticatedMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Cookie[] cookies = extractCookies(servletRequest);
        String jws = extractJws(cookies);

        try {
            Jws<Claims> claims = parseJws(jws);
            checkExpiration(claims);
            String memberId = claims.getPayload().get(jwtProperty.MEMBER_ID, String.class);
            return new AuthenticatedMember(Long.parseLong(memberId));

        } catch (JwtException e) {
            log.error("JWT validation failed", e);
            throw new Unauthorized();
        }
    }

    private Cookie[] extractCookies(HttpServletRequest servletRequest) {
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

    private String extractJws(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (jwtProperty.getCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new Unauthorized();
    }

    private Jws<Claims> parseJws(String jws) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperty.getKey()))
                .build()
                .parseSignedClaims(jws);
    }

    private void checkExpiration(Jws<Claims> claims) {
        Date expirationDate = new Date(claims.getPayload().get(jwtProperty.EXPIRATION, Long.class) * 1000);
        if (expirationDate.before(new Date())) {
            throw new Unauthorized();
        }
    }
}