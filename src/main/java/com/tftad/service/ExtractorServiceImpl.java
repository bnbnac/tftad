package com.tftad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tftad.config.property.ExtractorProperty;
import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.exception.ExtractorServerError;
import com.tftad.exception.InvalidRequest;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import com.tftad.request.external.Analysis;
import com.tftad.response.PositionOfPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtractorServiceImpl implements ExtractorService {

    private final PostRepository postRepository;
    private final QuestionRepository questionRepository;
    private final ExtractorProperty extractorProperty;

    @Override
    @Transactional
    public void generateQuestions(Long postId, List<String> result) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        for (int i = 0; i < result.size(); i += 2) {
            Question question = Question.builder()
                    .startTime(result.get(i))
                    .endTime(result.get(i + 1))
                    .post(post)
                    .build();

            questionRepository.save(question);
        }

        post.show();
        postRepository.save(post);
    }

    @Override
    public PositionOfPostResponse getPosition(Post post) {
        if (post.getPublished()) {
            return PositionOfPostResponse.builder()
                    .published(true)
                    .build();
        }

        ResponseEntity<JsonNode> response = queryPositionOnWorkingQueue(post);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ExtractorServerError();
        }
        int current = Integer.parseInt(response.getBody().get("current").asText());
        int initial = Integer.parseInt(response.getBody().get("initial").asText());

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
                .uri(extractorProperty.getUrl() + "/analysis")
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
    public boolean validatePostInExtractorCompletion(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);

        return post != null && !post.getPublished(); // enum 같은걸로 디테일을
    }

    @Override
    public Post validatePostBeforeGetPosition(Long memberId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        if (!memberId.equals(post.getMember().getId())) {
            throw new InvalidRequest("postId", "게시글의 작성자만 작업상황을 조회할 수 있습니다");
        }
        return post;
    }

    private ResponseEntity<JsonNode> queryPositionOnWorkingQueue(Post post) {
        WebClient client = WebClient.create();

        String uri = UriComponentsBuilder.fromUriString(extractorProperty.getUrl() + "/position")
                .queryParam("id", post.getId())
                .build().toUriString();

        return client.get()
                .uri(uri)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }
}
