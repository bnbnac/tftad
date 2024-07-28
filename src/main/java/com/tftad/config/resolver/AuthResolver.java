package com.tftad.config.resolver;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.RefreshRequest;
import com.tftad.config.property.AuthProperty;
import com.tftad.exception.Unauthorized;
import com.tftad.utility.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
        String accessToken = jwtUtils.extractValueByCookieName(cookies, authProperty.getAccessTokenCookieName());

        if (parameter.getParameterType().equals(AuthenticatedMember.class)) {
            return getAuthenticatedMember(accessToken);
        } else {
            String refreshToken = jwtUtils.extractValueByCookieName(cookies, authProperty.getRefreshTokenCookieName());
            return getRefreshRequest(accessToken, refreshToken);
        }
    }

    public AuthenticatedMember getAuthenticatedMember(String accessToken) {
        try {
            Claims payload = jwtUtils.validateAndParseToken(accessToken).getPayload();
            Long memberId = extractMemberId(payload);
            return createAuthenticatedMember(memberId);
        } catch (ExpiredJwtException e) {
            throw new Unauthorized("Expired JWT token");
        }
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

    private RefreshRequest getRefreshRequest(String accessToken, String refreshToken) {
        Claims payload = getClaimsEvenIfExpired(accessToken);
        Long memberId = extractMemberId(payload);
        return createRefreshRequest(refreshToken, memberId);
    }

    public Claims getClaimsEvenIfExpired(String accessToken) {
        try {
            return jwtUtils.validateAndParseToken(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private RefreshRequest createRefreshRequest(String refreshToken, Long memberId) {
        return RefreshRequest.builder()
                .token(refreshToken)
                .memberId(memberId)
                .build();
    }
}