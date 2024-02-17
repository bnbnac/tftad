package com.tftad.config.resolver;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.exception.Unauthorized;
import com.tftad.util.JwtUtility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final JwtProperty jwtProperty;
    private final GoogleOAuthProperty googleOAuthProperty;
    private final JwtUtility jwtUtility;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthenticatedMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        Cookie[] cookies = jwtUtility.extractCookiesFromRequest(servletRequest);
        String jws = jwtUtility.extractValueByCookieName(cookies, jwtProperty.getCookieName());

        try {
            Jws<Claims> parsedJws = jwtUtility.parseJws(jws);
            Claims payload = parsedJws.getPayload();

            jwtUtility.verifyExpiration(payload, jwtProperty.EXPIRATION);
            String memberId = payload.get(jwtProperty.MEMBER_ID, String.class);
            String code = payload.get(googleOAuthProperty.AUTHORIZATION_CODE, String.class);

            return AuthenticatedMember.builder()
                    .id(memberId)
                    .authorizationCode(code)
                    .build();
        } catch (JwtException e) {
            log.error("JWT validation failed", e);
            throw new Unauthorized();
        }
    }
}