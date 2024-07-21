package com.tftad.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MailVerify {
    @Email
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @NotBlank(message = "코드를 입력해주세요")
    private String code;
}
