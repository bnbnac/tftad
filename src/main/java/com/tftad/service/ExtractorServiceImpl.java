package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.exception.PostNotFound;
import com.tftad.repository.PostRepository;
import com.tftad.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtractorServiceImpl implements ExtractorService {

    private final PostRepository postRepository;
    private final QuestionRepository questionRepository;

    @Override
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFound::new);
    }

    @Override
    public void generateQuestions(Post post, List<String> result) {
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

    // INSERT INTO channel (youtube_channel_id, member_id, title) VALUES ('UCdrwiYsO52W3rrUS_wHW9mA', 2, 'hello');

}
