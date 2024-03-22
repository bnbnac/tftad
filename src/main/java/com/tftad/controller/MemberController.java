package com.tftad.controller;


import com.tftad.config.data.AuthenticatedMember;
import com.tftad.domain.MemberEditDto;
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
    public MemberResponse edit(AuthenticatedMember authenticatedMember, @RequestBody MemberEdit memberEdit) {
        MemberEditDto memberEditDto = memberEdit.toMemberEditDtoBuilder()
                .memberId(authenticatedMember.getId())
                .build();
        return memberService.edit(memberEditDto);
    }
}
