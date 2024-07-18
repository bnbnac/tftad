package com.tftad.controller;

import com.tftad.request.MailSend;
import com.tftad.request.MailVerify;
import com.tftad.service.AuthCodeSendService;
import com.tftad.service.CodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CodeController {

    private final AuthCodeSendService authCodeSendService;
    private final CodeService codeService;

    @PostMapping("/code/mail")
    public void send(@RequestBody @Valid MailSend mailSend) {
        codeService.validateMail(mailSend.getEmail());
        String authCode = authCodeSendService.sendCode(mailSend.getEmail());
        codeService.create(authCode, mailSend.getEmail());
    }

    @GetMapping("/code/mail")
    public void mailVerify(@RequestBody MailVerify mailVerify) {
        codeService.verify(mailVerify.getAuthCode(), mailVerify.getEmail());
    }
}
