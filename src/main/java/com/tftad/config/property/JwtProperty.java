package com.tftad.config.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    private byte[] key;
    private Long maxAgeInMinutes;
    private boolean cookieSecure;

    public void setKey(String key) {
        this.key = Base64.getDecoder().decode(key);
    }

    public void setMaxAgeInMinutes(Long maxAgeInMinutes) {
        this.maxAgeInMinutes = maxAgeInMinutes;
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }
}
