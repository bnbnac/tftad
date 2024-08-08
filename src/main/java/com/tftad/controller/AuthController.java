package com.tftad.controller;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.config.data.RefreshRequest;
import com.tftad.config.property.AuthProperty;
import com.tftad.config.property.JwtProperty;
import com.tftad.domain.RefreshTokenCreateDto;
import com.tftad.exception.TokenNotFound;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import com.tftad.response.TokenResponse;
import com.tftad.service.AuthService;
import com.tftad.service.RefreshTokenService;
import com.tftad.utility.JwtUtils;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final AuthProperty authProperty;
    private final JwtProperty jwtProperty;
    private final JwtUtils jwtUtils;

    @PostMapping("/auth/signup")
    public Long signup(@RequestBody @Valid Signup signup) {
        return authService.signup(signup);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@RequestBody @Valid Login login, HttpServletRequest request) {
        Long memberId = authService.login(login);
        RefreshTokenCreateDto refreshTokenCreateDto = createRefreshTokenCreateDto(request, memberId);
        String refreshToken = refreshTokenService.save(refreshTokenCreateDto);

        JwtBuilder accessTokenBuilder = Jwts.builder().claim(AuthProperty.MEMBER_ID, String.valueOf(memberId));
        String accessTokenJws = jwtUtils.generateJws(accessTokenBuilder, jwtProperty.getMaxAgeInMinutes());

        ResponseCookie accessTokenCookie = createAccessTokenCookie(accessTokenJws);
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    private RefreshTokenCreateDto createRefreshTokenCreateDto(HttpServletRequest request, Long memberId) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");

        return RefreshTokenCreateDto.builder()
                .memberId(memberId)
                .clientIp(clientIp)
                .userAgent(userAgent)
                .build();
    }

    private ResponseCookie createAccessTokenCookie(String accessTokenJws) {
        return jwtUtils.createCookie(
                authProperty.getAccessTokenCookieName(),
                accessTokenJws,
                authProperty.getAccessTokenCookieMaxAgeInDays()
        );
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return jwtUtils.createCookie(
                authProperty.getRefreshTokenCookieName(),
                refreshToken,
                authProperty.getRefreshTokenCookieMaxAgeInDays()
        );
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<Void> refresh(RefreshRequest refreshRequest) {
        refreshTokenService.verify(refreshRequest);

        JwtBuilder accessTokenBuilder = Jwts.builder()
                .claim(AuthProperty.MEMBER_ID, String.valueOf(refreshRequest.getMemberId()));

        String accessTokenJws = jwtUtils.generateJws(accessTokenBuilder, jwtProperty.getMaxAgeInMinutes());
        ResponseCookie accessTokenCookie = createAccessTokenCookie(accessTokenJws);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .build();
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity<Void> logMeOut(RefreshRequest refreshRequest) {
        try {
            authService.logout(refreshRequest);
        } catch (TokenNotFound e) {
            log.info("이미 만료된 토큰입니다. 쿠키를 삭제합니다. token: {}", refreshRequest.getToken());
        }

        ResponseCookie accessTokenCookie = createExpiredCookie(authProperty.getAccessTokenCookieName());
        ResponseCookie refreshTokenCookie = createExpiredCookie(authProperty.getRefreshTokenCookieName());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    private ResponseCookie createExpiredCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .domain(authProperty.getTftadDomain())
                .path("/")
                .httpOnly(true)
                .secure(jwtProperty.isCookieSecure())
                .maxAge(0)
                .sameSite("strict")
                .build();
    }

    @GetMapping("/auth/tokens")
    public List<TokenResponse> getCurrentTokens(AuthenticatedMember authenticatedMember) {
        return refreshTokenService.getListOf(authenticatedMember.getId());
    }

    @DeleteMapping("/auth/logout/{tokenId}")
    public void logout(@PathVariable Long tokenId, AuthenticatedMember authenticatedMember) {
        authService.logout(tokenId, authenticatedMember);
    }
}
