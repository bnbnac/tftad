package com.tftad.repository;

import com.tftad.domain.Post;
import com.tftad.request.PostSearch;
import com.tftad.response.PostResponseDetail;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);

    List<Post> getListOfMember(Long memberId, PostSearch postSearch);

    Optional<PostResponseDetail> getPostWithDetails(Long postId);
}
