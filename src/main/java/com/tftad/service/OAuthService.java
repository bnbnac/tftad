package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.property.GoogleOAuthProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOAuthProperty googleOAuthProperty;

    public JsonNode queryChannelResource(String code) {
        String accessToken = queryAccessToken(code);

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

    private String queryAccessToken(String code) {
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

    public String queryVideoResourceToGetChannelId(String videoId) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(googleOAuthProperty.getYoutubeResourceUrl() + "/videos")
                .queryParam("part", "snippet")
                .queryParam("id", videoId)
                .queryParam("key", googleOAuthProperty.getApiKey())
                .build().toUriString();

        return client.get()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block()
                .getBody()
                .get("items")
                .get(0)
                .get("snippet")
                .get("channelId")
                .asText();
    }
}
