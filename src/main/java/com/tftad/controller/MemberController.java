package com.tftad.controller;


import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.MemberEditDto;
import com.tftad.exception.InvalidRequest;
import com.tftad.request.MemberEdit;
import com.tftad.response.MemberResponse;
import com.tftad.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/{memberId}")
    public MemberResponse get(@PathVariable Long memberId) {
        return memberService.get(memberId);
    }

    @GetMapping("/members/me")
    public MemberResponse get(AuthenticatedMember authenticatedMember) {
        return memberService.get(authenticatedMember.getId());
    }

    @PatchMapping("members/{memberId}")
    public void edit(AuthenticatedMember authenticatedMember,
                               @RequestBody MemberEdit memberEdit, @PathVariable Long memberId) {
        if (!authenticatedMember.getId().equals(memberId)) {
            throw new InvalidRequest("memberId", "본인의 정보만 수정할 수 있습니다");
        }
        MemberEditDto memberEditDto = memberEdit.toMemberEditDtoBuilder()
                .memberId(authenticatedMember.getId())
                .build();
        memberService.edit(memberEditDto);
    }
}
