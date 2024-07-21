package com.tftad.service;

import com.tftad.config.property.AuthProperty;
import com.tftad.domain.Code;
import com.tftad.exception.InvalidRequest;
import com.tftad.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CodeService {

    private final AuthProperty authProperty;
    private final CodeRepository codeRepository;

    public void create(String code, String email) {
        List<Code> codes = codeRepository.findByEmail(email);
        codeRepository.deleteAll(codes);

        Code newCode = createCode(code, email, authProperty.getAuthCodeDurationMinutes());
        codeRepository.save(newCode);
    }

    public Code createCode(String code, String email, Long durationMinutes) {
        return Code.builder()
                .email(email)
                .code(code)
                .durationMinutes(durationMinutes)
                .build();
    }

    @Transactional
    public void verify(String code, String email) {
        Code foundCode = findCode(code, email);
        if (!isValid(foundCode)) {
            codeRepository.delete(foundCode);
            return;
        }

        foundCode.auth();
    }

    private boolean isValid(Code code) {
        validateCodeExpiration(code);
        return true;
    }

    public Code findCode(String code, String mail) {
        return codeRepository.findTopByCodeAndEmailOrderByCreatedAtDesc(code, mail)
                .orElseThrow(() -> new InvalidRequest("code", "code does not match"));
    }

    private void validateCodeExpiration(Code foundCode) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(foundCode.getCreatedAt(), now);

        if (duration.getSeconds() > authProperty.getEmailAuthCodeDurationMinutes() * 60) {
            throw new InvalidRequest("code", "expired code");
        }
    }
}
