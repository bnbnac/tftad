package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.MemberRepository;
import com.tftad.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(MemberNotFound::new);
    }

    public MemberResponse get(Long memberId) {
        return new MemberResponse(getMemberById(memberId));
    }
}
