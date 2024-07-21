package com.tftad.controller;

import com.tftad.exception.MailSendFail;
import com.tftad.request.MailSend;
import com.tftad.request.MailVerify;
import com.tftad.service.AuthCodeSendService;
import com.tftad.service.CodeService;
import com.tftad.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CodeController {

    private final MemberService memberService;
    private final AuthCodeSendService authCodeSendService;
    private final CodeService codeService;

    @PostMapping("/code/mail")
    public void send(@RequestBody @Valid MailSend mailSend) {
        memberService.validateSignedUpMail(mailSend.getEmail());
        try {
            String code = authCodeSendService.sendCode(mailSend.getEmail());
            codeService.create(code, mailSend.getEmail());
        } catch (Exception e) {
            throw new MailSendFail();
        }
    }

    @PostMapping("/code/mail/verification")
    public void mailVerify(@RequestBody MailVerify mailVerify) {
        codeService.verify(mailVerify.getCode(), mailVerify.getEmail());
    }
}
