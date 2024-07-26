package com.tftad.config.resolver;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.RefreshRequest;
import com.tftad.config.property.AuthProperty;
import com.tftad.utility.JwtUtils;
import io.jsonwebtoken.Claims;
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

    private final JwtUtils jwtUtils;
    private final AuthProperty authProperty;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthenticatedMember.class)
                || parameter.getParameterType().equals(RefreshRequest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Cookie[] cookies = jwtUtils.extractCookiesFromRequest(webRequest.getNativeRequest(HttpServletRequest.class));
        Claims payload = getAccessTokenPayload(cookies);
        Long memberId = extractMemberId(payload);

        if (parameter.getParameterType().equals(AuthenticatedMember.class)) {
            jwtUtils.validateExpiration(payload);
            return createAuthenticatedMember(memberId);
        } else {
            return createRefreshRequest(cookies, memberId);
        }
    }

    private Claims getAccessTokenPayload(Cookie[] cookies) {
        String jws = jwtUtils.extractValueByCookieName(cookies, authProperty.getAccessTokenCookieName());
        return jwtUtils.parseJws(jws).getPayload();
    }

    private Long extractMemberId(Claims payload) {
        String memberId = payload.get(AuthProperty.MEMBER_ID, String.class);
        return Long.parseLong(memberId);
    }

    private AuthenticatedMember createAuthenticatedMember(Long memberId) {
        return AuthenticatedMember.builder()
                .id(memberId)
                .build();
    }

    private RefreshRequest createRefreshRequest(Cookie[] cookies, Long memberId) {
        String refreshToken = jwtUtils.extractValueByCookieName(cookies, authProperty.getRefreshTokenCookieName());
        return RefreshRequest.builder()
                .token(refreshToken)
                .memberId(memberId)
                .build();
    }
}