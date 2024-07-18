package com.tftad.service;

import com.tftad.config.property.AuthProperty;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailAuthCodeSendService implements AuthCodeSendService {

    private final JavaMailSender javaMailSender;

    @Override
    public String sendCode(String toMail) {
        String code = createCode();
        MimeMessage message = CreateMessage(code, toMail);
        javaMailSender.send(message);
        return code;
    }

    private String createCode() {
        return String.valueOf((int) (Math.random() * 900_000) + 100_000);
    }

    private MimeMessage CreateMessage(String code, String toMail) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(AuthProperty.MAIL_SENDER);
            message.setRecipients(MimeMessage.RecipientType.TO, toMail);
            message.setSubject("이메일 인증");
            message.setText(generateEmailBody(code),"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    private String generateEmailBody(String code) {
        return "<h3>" + "tftad에서 요청하신 인증 번호 발송" + "</h3>" +
                "<h1>" + code + "</h1>" +
                "<h3>" + "이용해주셔서 감사합니다." + "</h3>";
    }
}
