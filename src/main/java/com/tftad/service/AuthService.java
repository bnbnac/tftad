package com.tftad.service;

import com.tftad.config.JwtProperty;
import com.tftad.domain.Member;
import com.tftad.exception.InvalidLoginInformation;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.MemberRepository;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperty jwtProperty;

    @Transactional
    public Long login(Login login) {
        Member member = memberRepository.findByEmail(login.getEmail())
                .orElseThrow(InvalidLoginInformation::new);

        if (!passwordEncoder.matches(login.getPassword(), member.getPassword())) {
            throw new InvalidLoginInformation();
        }

        return member.getId();
    }

    public void signup(Signup signup) {
        Optional<Member> memberOptional = memberRepository.findByEmail(signup.getEmail());
        if (memberOptional.isPresent()) {
            throw new InvalidRequest("email", "이미 가입된 이메일입니다");
        }

        String encodedPassword = passwordEncoder.encode(signup.getPassword());

        Member member = Member.builder()
                .email(signup.getEmail())
                .name(signup.getName())
                .password(encodedPassword)
                .build();
        memberRepository.save(member);
    }

    // separate JWT?
    // ---
    public String generateJws(Long memberId) {
        return Jwts.builder()
                .claim(jwtProperty.MEMBER_ID, String.valueOf(memberId))
                .expiration(Date.from(Instant.now().plus(jwtProperty.getCookieMaxAgeInDays(), ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(jwtProperty.getKey()))
                .compact();
    }

    // 가볍게 유틸로 빼야?
    public ResponseCookie generateJwtCookie(String jws) {
        return ResponseCookie.from(jwtProperty.getCookieName(), jws)
                .domain("localhost")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(Duration.ofDays(jwtProperty.getCookieMaxAgeInDays()))
                .sameSite("strict")
                .build();
    }
}
