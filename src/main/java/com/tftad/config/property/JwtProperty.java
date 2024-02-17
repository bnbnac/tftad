package com.tftad.config.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    public String MEMBER_ID = "member_id";
    public String EXPIRATION = "exp";

    private byte[] key;
    private String cookieName;
    private int cookieMaxAgeInDays;
    private String domain;

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setKey(String key) {
        this.key = Base64.getDecoder().decode(key);
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public void setCookieMaxAgeInDays(int cookieMaxAgeInDays) {
        this.cookieMaxAgeInDays = cookieMaxAgeInDays;
    }
}
