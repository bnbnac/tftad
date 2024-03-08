package com.tftad.service;

import com.tftad.domain.Member;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(MemberNotFound::new);
    }
}
