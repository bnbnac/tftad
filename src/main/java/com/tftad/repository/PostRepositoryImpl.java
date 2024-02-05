package com.tftad.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tftad.domain.Post;
import com.tftad.request.PostSearch;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.tftad.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset())
                .orderBy(post.id.desc())
                .fetch();
    }
}
