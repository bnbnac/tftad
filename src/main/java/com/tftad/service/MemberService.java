package com.tftad.service;

import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.Member;
import com.tftad.domain.MemberEditor;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.MemberNotFound;
import com.tftad.repository.MemberRepository;
import com.tftad.request.MemberEdit;
import com.tftad.response.MemberResponse;
import com.tftad.response.MemberResponseDetail;
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
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFound::new);
        return new MemberResponse(member);
    }

    @Transactional
    public MemberResponseDetail getMemberDetails(AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);
        return memberRepository.getMemberWithDetails(member.getId());
    }

    @Transactional
    public void edit(Long memberId, MemberEdit memberEdit, AuthenticatedMember authenticatedMember) {
        Member member = authService.check(authenticatedMember);
        validateMemberOwner(memberId, authenticatedMember);

        String password = null;
        if (memberEdit.getPassword() != null) {
            password = passwordEncoder.encode(memberEdit.getPassword());
        }
        MemberEditor memberEditor = createEditor(password, memberEdit, member);
        member.edit(memberEditor);
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

    public Member getDeletedMember() {
        return memberRepository.findById(-1L)
                .orElseThrow(() -> new InvalidRequest("memberId", "no member with id -1"));
    }
}
