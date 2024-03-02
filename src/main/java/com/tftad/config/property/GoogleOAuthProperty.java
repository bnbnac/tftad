package com.tftad.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth.google")
public class GoogleOAuthProperty {

    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String YOUTUBE_CHANNEL_URL_PREFIX = "https://www.youtube.com/channel/";

    private String cookieName;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String tokenUrl;
    private int cookieMaxAgeInDays;
    private String channelResourceUrl;
    private String videoResourceUrl;
    private String apiKey;
}
