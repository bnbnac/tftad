package com.tftad.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth.google")
public class GoogleOAuthProperty {
    public static final int MAX_VIDEO_DURATION_MINUTES = 180;

    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String tokenUrl;
    private String youtubeResourceUrl;
    private String apiKey;
}
