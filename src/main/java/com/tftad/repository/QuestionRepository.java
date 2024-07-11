package com.tftad.repository;

import com.tftad.domain.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    List<Question> findByPostId(Long postId);

    List<Question> findByPostIdOrderByStartTimeAsc(Long postId);
}
