package com.tftad.repository;

import com.tftad.domain.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long>, PostRepositoryCustom {

}
