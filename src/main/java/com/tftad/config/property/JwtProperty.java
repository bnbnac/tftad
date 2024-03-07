package com.tftad.config.property;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    private byte[] key;
    private int maxAgeInDays;

    public void setMaxAgeInDays(int maxAgeInDays) {
        this.maxAgeInDays = maxAgeInDays;
    }

    public void setKey(String key) {
        this.key = Base64.getDecoder().decode(key);
    }

}
