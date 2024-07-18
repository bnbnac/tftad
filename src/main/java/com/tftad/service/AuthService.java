package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Code;
import com.tftad.domain.Member;
import com.tftad.exception.InvalidLoginInformation;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.CodeRepository;
import com.tftad.repository.MemberRepository;
import com.tftad.request.Login;
import com.tftad.request.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final CodeRepository codeRepository;
    private final PasswordEncoder passwordEncoder;

    public Member check(AuthenticatedMember authenticatedMember) {
        return memberRepository.findById(authenticatedMember.getId()).orElseThrow(MemberNotFound::new);
    }

    public Long login(Login login) {
        Member member = memberRepository.findByEmail(login.getEmail()).orElseThrow(InvalidLoginInformation::new);

        if (!passwordEncoder.matches(login.getPassword(), member.getPassword())) {
            throw new InvalidLoginInformation();
        }

        return member.getId();
    }

    public Long signup(Signup signup) {
        validateSignedUpMail(signup.getEmail());
        validateCode(signup.getCode(), signup.getEmail());

        String encodedPassword = passwordEncoder.encode(signup.getPassword());

        Member member = Member.builder()
                .email(signup.getEmail())
                .name(signup.getName())
                .password(encodedPassword)
                .build();
        return memberRepository.save(member).getId();
    }

    private void validateSignedUpMail(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new InvalidRequest("email", "이미 가입된 이메일입니다");
                });
    }

    private void validateCode(String code, String email) {
        Code foundCode = findCode(code, email);
        validateCodeExpiration(foundCode);
        validateAuthedCode(foundCode);
    }

    private Code findCode(String code, String email) {
        return codeRepository.findTopByCodeAndEmailOrderByCreatedAtDesc(code, email)
                .orElseThrow(() -> {
                    throw new InvalidRequest("email", "인증되지 않은 이메일입니다.");
                });
    }

    private void validateCodeExpiration(Code foundCode) {
        if (foundCode.isExpired()) {
            throw new InvalidRequest("email", "인증이 만료되었습니다.");
        }
    }

    private void validateAuthedCode(Code foundCode) {
        if (!foundCode.isAuthed()) {
            throw new InvalidRequest("email", "인증코드를 생성후 인증해주세요.");
        }
    }
}
