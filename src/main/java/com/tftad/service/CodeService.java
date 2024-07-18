package com.tftad.service;

import com.tftad.config.property.AuthProperty;
import com.tftad.domain.Code;
import com.tftad.exception.CodeNotFound;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.CodeRepository;
import com.tftad.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class CodeService {

    private final AuthProperty authProperty;
    private final CodeRepository codeRepository;
    private final MemberRepository memberRepository;

    public void create(String authCode, String authMail) {
        codeRepository.save(createCode(authCode, authMail, authProperty.getAuthCodeDurationMinutes()));
    }

    public Code createCode(String code, String authMail, Long durationMinutes) {
        return Code.builder()
                .email(authMail)
                .code(code)
                .durationMinutes(durationMinutes)
                .build();
    }

    @Transactional
    public void verify(String authCode, String authMail) {
        Code foundCode = codeRepository.findTopByEmailOrderByCreatedAtDesc(authMail).orElseThrow(CodeNotFound::new);

        try {
            validateCode(authCode, foundCode);
            validateCodeExpiration(foundCode);
        } catch (InvalidRequest e) {
            codeRepository.delete(foundCode);
        }

        foundCode.auth();
    }

    private void validateCode(String authCode, Code foundCode) {
        if (!foundCode.getCode().equals(authCode)) {
            throw new InvalidRequest("code", "code does not match");
        }
    }

    private void validateCodeExpiration(Code foundCode) {
        LocalDateTime createdAt = foundCode.getCreatedAt();

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        if (duration.getSeconds() > authProperty.getEmailAuthCodeDurationMinutes() * 60) {
            throw new InvalidRequest("code", "expired code");
        }
    }

    public void validateMail(String email) {
        validateSignedUpMail(email);
        validateOverRequestedMail(email);
    }

    private void validateSignedUpMail(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new InvalidRequest("email", "이미 가입된 이메일입니다");
                });
    }

    private void validateOverRequestedMail(String email) {
        if (codeRepository.countByEmail(email) > authProperty.getLimitEmailAuthCodeRequest()) {
            throw new InvalidRequest("email", "너무 많이 요청하셨습니다. 잠시 후 다시 시도해주세요");
        }
    }
}
