package com.tftad.config.resolver;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.OAuthedMember;
import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.exception.Unauthorized;
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

import static com.tftad.utility.Utility.*;


@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final JwtProperty jwtProperty;
    private final GoogleOAuthProperty googleOAuthProperty;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthenticatedMember.class)
                || parameter.getParameterType().equals(OAuthedMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Cookie[] cookies = extractCookiesFromRequest(webRequest.getNativeRequest(HttpServletRequest.class));

        if (parameter.getParameterType().equals(AuthenticatedMember.class)) {
            return generateAuthenticatedMember(cookies);
        }

        return generateOAuthedMember(cookies);
    }

    private OAuthedMember generateOAuthedMember(Cookie[] cookies) {
        AuthenticatedMember authenticatedMember = generateAuthenticatedMember(cookies);
        String memberId = String.valueOf(authenticatedMember.getId());

        String jws = extractValueByCookieName(cookies, googleOAuthProperty.getCookieName());
        if (jws.isBlank()) {
            throw new Unauthorized();
        }
        Claims payload = parseJws(jws, jwtProperty.getKey()).getPayload();

        verifyExpiration(payload);

        String code = payload.get(GoogleOAuthProperty.AUTHORIZATION_CODE, String.class);
        return OAuthedMember.builder()
                .id(Long.parseLong(memberId))
                .authorizationCode(code)
                .build();
    }

    private AuthenticatedMember generateAuthenticatedMember(Cookie[] cookies) {
        String jws = extractValueByCookieName(cookies, jwtProperty.getCookieName());
        if (jws.isBlank()) {
            throw new Unauthorized();
        }
        Claims payload = parseJws(jws, jwtProperty.getKey()).getPayload();

        verifyExpiration(payload);

        String memberId = payload.get(JwtProperty.MEMBER_ID, String.class);
        return AuthenticatedMember.builder()
                .id(Long.parseLong(memberId))
                .build();
    }
}