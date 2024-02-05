package com.tftad.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Base64;

@Data
@ConfigurationProperties(prefix = "jwt")
public class AppConfig {

    private byte[] key;

    public byte[] getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = Base64.getDecoder().decode(key);
    }
}
