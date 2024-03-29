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

    private String googleCookieName;
    private int googleCookieMaxAgeInDays;
    private String tftadCookieName;
    private int tftadCookieMaxAgeInDays;
    private String tftadDomain;

}
