package com.tftad.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    public String MEMBER_ID = "member_id";
    public String EXPIRATION = "exp";

    private byte[] key;
    private String cookieName;
    private int cookieMaxAgeInDays;

    public int getCookieMaxAgeInDays() {
        return cookieMaxAgeInDays;
    }

    public void setCookieMaxAgeInDays(int cookieMaxAgeInDays) {
        this.cookieMaxAgeInDays = cookieMaxAgeInDays;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = Base64.getDecoder().decode(key);
    }
}
