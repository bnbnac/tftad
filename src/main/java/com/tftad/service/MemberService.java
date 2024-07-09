package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Member;
import com.tftad.domain.MemberEditor;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.MemberRepository;
import com.tftad.request.MemberEdit;
import com.tftad.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse get(Long memberId) {
        return new MemberResponse(memberRepository.findById(memberId).orElseThrow(MemberNotFound::new));
    }

    @Transactional
    public MemberResponse get(AuthenticatedMember authenticatedMember) {
        Member member = authService.checkMember(authenticatedMember);
        return new MemberResponse(member);
    }

    @Transactional
    public void edit(Long memberId, MemberEdit memberEdit, AuthenticatedMember authenticatedMember) {
        Member member = authService.checkMember(authenticatedMember);
        validateMemberOwner(memberId, authenticatedMember);

        String password = null;
        if (memberEdit.getPassword() != null) {
            password = passwordEncoder.encode(memberEdit.getPassword());
        }
        MemberEditor memberEditor = createEditor(password, memberEdit, member);
        member.edit(memberEditor);
        memberRepository.save(member);
    }

    private void validateMemberOwner(Long memberId, AuthenticatedMember authenticatedMember) {
        if (!authenticatedMember.getId().equals(memberId)) {
            throw new InvalidRequest("memberId", "소유자가 아닙니다");
        }
    }

    private MemberEditor createEditor(String password, MemberEdit memberEdit, Member member) {
        return member.toEditorBuilder()
                .name(memberEdit.getName())
                .password(password)
                .build();
    }
}
