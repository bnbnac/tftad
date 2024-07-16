package com.tftad.service;

import com.tftad.domain.Post;
import com.tftad.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChannelInheritServiceImpl implements ChannelInheritService {

    @PersistenceContext
    private final EntityManager entityManager;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void inheritPostsOfChannel(Long channelId, Long memberId) {
        List<Post> posts = postRepository.findByChannelId(channelId);

        if (posts.size() > 1000) {
            bulkInheritPostsOfChannel(channelId, memberId);
            return;
        }

        for (Post post : posts) {
            post.inherit(memberId);
        }
    }

    private void bulkInheritPostsOfChannel(Long channelId, Long memberId) {
        String jpql = "UPDATE Post p SET p.memberId = :memberId WHERE p.channelId = :channelId";

        entityManager.createQuery(jpql)
                .setParameter("memberId", memberId)
                .setParameter("channelId", channelId)
                .executeUpdate();
    }
}
