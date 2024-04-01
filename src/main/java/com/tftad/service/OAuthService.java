package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.property.GoogleOAuthProperty;
import com.tftad.exception.InvalidRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOAuthProperty googleOAuthProperty;

    public JsonNode queryChannelResource(String accessToken) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(googleOAuthProperty.getYoutubeResourceUrl() + "/channels")
                .queryParam("part", "snippet")
                .queryParam("mine", true)
                .build().toUriString();

        return client.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(JsonNode.class)
                .block()
                .getBody();
    }

    public String queryAccessToken(String code) {
        WebClient client = WebClient.create();

        return client.post()
                .uri(googleOAuthProperty.getTokenUrl())
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", googleOAuthProperty.getClientId())
                        .with("client_secret", googleOAuthProperty.getClientSecret())
                        .with("redirect_uri", googleOAuthProperty.getRedirectUrl())
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .toEntity(JsonNode.class)
                .block()
                .getBody()
                .get("access_token")
                .asText();
    }

    public JsonNode queryVideoResource(String videoId) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(googleOAuthProperty.getYoutubeResourceUrl() + "/videos")
                .queryParam("part", "snippet")
                .queryParam("part", "contentDetails")
                .queryParam("id", videoId)
                .queryParam("key", googleOAuthProperty.getApiKey())
                .build().toUriString();

        return client.get()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block()
                .getBody();
    }

    public String validateVideoAndGetYoutubeChannelId(JsonNode videoResource) {
        String videoDuration = getVideoDurationFromVideoResource(videoResource);
        validateVideoLength(videoDuration, GoogleOAuthProperty.MAX_VIDEO_DURATION_MINUTES);
        return getYoutubeChannelIdFromVideoResource(videoResource);
    }

    public void validateVideoLength(String durationString, int maxMinutes) {
        Duration duration = Duration.parse(durationString);
        long durationMinutes = duration.toMinutes();
        if (durationMinutes > maxMinutes) {
            throw new InvalidRequest("videoId", "video 길이는 " + maxMinutes + "분을 넘지 않아야 합니다");
        }
    }

    private String getVideoDurationFromVideoResource(JsonNode videoResource) {
        return videoResource.get("items")
                .get(0)
                .get("contentDetails")
                .get("duration")
                .asText();
    }

    public String getYoutubeChannelIdFromVideoResource(JsonNode videoResource) {
        return videoResource.get("items")
                .get(0)
                .get("snippet")
                .get("channelId")
                .asText();
    }
}
