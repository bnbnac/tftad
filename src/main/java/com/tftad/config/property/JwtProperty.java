package com.tftad.config.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    private byte[] key;
    private int maxAgeInDays;
    private boolean cookieSecure;

    public void setKey(String key) {
        this.key = Base64.getDecoder().decode(key);
    }

    public void setMaxAgeInDays(int maxAgeInDays) {
        this.maxAgeInDays = maxAgeInDays;
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }
}
