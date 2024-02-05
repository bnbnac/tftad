package com.tftad.config;

import com.tftad.config.data.MemberSession;
import com.tftad.exception.Unauthorized;
import com.tftad.repository.SessionRepository;
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

    private final SessionRepository sessionRepository;
    private final AppConfig appConfig;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(MemberSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            log.error("servletRequest is null");
            throw new Unauthorized();
        }

        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null || cookies.length == 0) {
            log.error("cookie is null");
            throw new Unauthorized();
        }

        String jws = null;
        for (Cookie cookie : cookies) {
            if ("ML".equals(cookie.getName())) {
                jws = cookie.getValue();
                break;
            }
        }

        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(appConfig.getKey()))
                    .build()
                    .parseSignedClaims(jws);

            String memberId = claims.getPayload().get("member_id", String.class);
            Date expirationDate = new Date(claims.getPayload().get("exp", Long.class) * 1000);

            if (expirationDate.compareTo(new Date()) < 0) {
                throw new Unauthorized();
            }
            return new MemberSession(Long.parseLong(memberId));

        } catch (JwtException e) {
            throw new Unauthorized();
        }
    }
}