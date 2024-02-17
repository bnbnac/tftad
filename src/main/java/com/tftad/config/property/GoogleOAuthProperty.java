package com.tftad.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth.google")
public class GoogleOAuthProperty {

    public String AUTHORIZATION_CODE = "authorization_code";

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String tokenUri;
    private int cookieMaxAgeInDays;
    private String resourceUri;
}
