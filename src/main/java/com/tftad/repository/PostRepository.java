package com.tftad.repository;

import com.tftad.domain.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findByVideoId(String videoId);

    List<Post> findByChannelId(Long channelId);
}
