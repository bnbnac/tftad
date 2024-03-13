package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.property.Urls;
import com.tftad.domain.Post;
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
    private final PostService postService;

    @Override
    public PositionOfPostResponse getPosition(Long postId) {
        Post post = postService.getPostById(postId);

        if (post.getPublished()) {
            return PositionOfPostResponse.builder()
                    .published(true)
                    .build();
        }

        ResponseEntity<JsonNode> response = queryPositionOnWorkingQueue(post);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ExtractorServerError();
        }
        int current = response.getBody().get("current").asInt();
        int initial = response.getBody().get("initial").asInt();

        return PositionOfPostResponse.builder()
                .current(current)
                .initial(initial)
                .published(false)
                .build();
    }

    @Override
    public void queryAnalysis(String videoId, Long postId) {
        Analysis analysis = Analysis.builder()
                .videoId(videoId)
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
    public ResponseEntity<JsonNode> queryDelete(Long postId) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(urls.getExtractorServer() + "/delete")
                .queryParam("id", postId)
                .build().toUriString();

        return client.delete()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    private ResponseEntity<JsonNode> queryPositionOnWorkingQueue(Post post) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(urls.getExtractorServer() + "/position")
                .queryParam("id", post.getId())
                .build().toUriString();

        return client.get()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }
}
