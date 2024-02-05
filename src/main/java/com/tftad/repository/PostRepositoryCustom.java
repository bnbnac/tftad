package com.tftad.repository;

import com.tftad.domain.Post;
import com.tftad.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
