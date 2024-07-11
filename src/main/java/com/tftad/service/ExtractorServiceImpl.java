package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.property.Urls;
import com.tftad.exception.ExtractorServerError;
import com.tftad.request.external.Analysis;
import com.tftad.response.PositionOfPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ExtractorServiceImpl implements ExtractorService {

    private final Urls urls;

    @Override
    public PositionOfPostResponse getPosition(Long postId) {
        ResponseEntity<JsonNode> response = queryPositionOnWorkingQueue(postId);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ExtractorServerError();
        }
        int curPosition = response.getBody().get("curPosition").asInt();
        int initialPosition = response.getBody().get("initialPosition").asInt();
        String curPostId = response.getBody().get("curPostId").asText();
        String curFrame = response.getBody().get("curFrame").asText();
        String totalFrame = response.getBody().get("totalFrame").asText();
        String state = response.getBody().get("state").asText();

        if (!String.valueOf(postId).equals(curPostId)) {
            return PositionOfPostResponse.builder()
                    .state("waiting")
                    .curPosition(String.valueOf(curPosition))
                    .initialPosition(String.valueOf(initialPosition))
                    .build();
        }

        return PositionOfPostResponse.builder()
                .curPosition(String.valueOf(curPosition))
                .initialPosition(String.valueOf(initialPosition))
                .state(state)
                .totalFrame(totalFrame)
                .curFrame(curFrame)
                .published(false)
                .build();
    }

    @Override
    public void getAnalysis(String videoId, Long memberId, Long postId) {
        Analysis analysis = Analysis.builder()
                .videoId(videoId)
                .memberId(memberId)
                .postId(postId)
                .build();

        WebClient client = WebClient.create();
        boolean ok = client.post()
                .uri(urls.getExtractorServer() + "/analysis")
                .bodyValue(analysis)
                .retrieve()
                .toBodilessEntity()
                .block()
                .getStatusCode()
                .is2xxSuccessful();
        if (!ok) {
            throw new ExtractorServerError();
        }
    }

    @Override
    public ResponseEntity<JsonNode> deleteAnalysisByPostId(Long memberId, Long postId) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(urls.getExtractorServer() + "/posts")
                .queryParam("memberId", memberId)
                .queryParam("postId", postId)
                .build().toUriString();

        return client.delete()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    private ResponseEntity<JsonNode> queryPositionOnWorkingQueue(Long postId) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(urls.getExtractorServer() + "/position")
                .queryParam("id", postId)
                .build().toUriString();

        return client.get()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    @Override
    public ResponseEntity<JsonNode> deleteAnalysisByQuestionFilename(Long memberId, Long postId, String filename) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(urls.getExtractorServer() + "/questions")
                .queryParam("memberId", memberId)
                .queryParam("postId", postId)
                .queryParam("filename", filename)
                .build().toUriString();

        return client.delete()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }
}
