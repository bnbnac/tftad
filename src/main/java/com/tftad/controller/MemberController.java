package com.tftad.controller;


import com.tftad.config.data.AuthenticatedMember;
import com.tftad.request.MemberEdit;
import com.tftad.response.MemberResponseDetail;
import com.tftad.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

//    @GetMapping("/members/{memberId}")
//    public MemberResponse get(@PathVariable Long memberId) {
//        return memberService.get(memberId);
//    } // 이걸 왜했지?

    @GetMapping("/members/me")
    public MemberResponseDetail get(AuthenticatedMember authenticatedMember) {
        return memberService.getMemberDetails(authenticatedMember);
    }

    @PatchMapping("members/{memberId}")
    public void edit(AuthenticatedMember authenticatedMember,
                     @RequestBody MemberEdit memberEdit, @PathVariable Long memberId) {
        memberService.edit(memberId, memberEdit, authenticatedMember);
    }
}
