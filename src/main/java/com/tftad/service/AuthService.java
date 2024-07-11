package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Member;
import com.tftad.exception.InvalidLoginInformation;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
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
    private final PasswordEncoder passwordEncoder;

    public Long login(Login login) {
        Member member = memberRepository.findByEmail(login.getEmail()).orElseThrow(InvalidLoginInformation::new);

        if (!passwordEncoder.matches(login.getPassword(), member.getPassword())) {
            throw new InvalidLoginInformation();
        }

        return member.getId();
    }

    public Long signup(Signup signup) {
        memberRepository.findByEmail(signup.getEmail())
                .ifPresent(m -> {
                    throw new InvalidRequest("email", "이미 가입된 이메일입니다");
                });

        String encodedPassword = passwordEncoder.encode(signup.getPassword());

        Member member = Member.builder()
                .email(signup.getEmail())
                .name(signup.getName())
                .password(encodedPassword)
                .build();
        return memberRepository.save(member).getId();
    }

    public Member check(AuthenticatedMember authenticatedMember) {
        return memberRepository.findById(authenticatedMember.getId()).orElseThrow(MemberNotFound::new);
    }
}
