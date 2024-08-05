package com.tftad.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthProperty {

    public static final String MEMBER_ID = "member_id";
    public static final String EXPIRATION = "exp";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String MAIL_SENDER = "tftad.com@gmail.com";

    private String accessTokenCookieName;
    private Long accessTokenCookieMaxAgeInDays;
    private String refreshTokenCookieName;
    private Long refreshTokenCookieMaxAgeInDays;
    private String tftadDomain;
    private String allowedOrigins;
    private Long authCodeDurationMinutes;
    private Long emailAuthCodeDurationMinutes;
    private Long limitEmailAuthCodeRequest;

}
