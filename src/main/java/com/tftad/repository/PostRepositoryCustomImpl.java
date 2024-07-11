package com.tftad.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tftad.domain.Channel;
import com.tftad.domain.Post;
import com.tftad.domain.Question;
import com.tftad.request.PostSearch;
import com.tftad.response.ChannelResponse;
import com.tftad.response.PostResponse;
import com.tftad.response.PostResponseDetail;
import com.tftad.response.QuestionResponse;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tftad.domain.QChannel.channel;
import static com.tftad.domain.QPost.post;
import static com.tftad.domain.QQuestion.question;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
                .where(post.published.isTrue())
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset())
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public List<Post> getListOfMember(Long memberId, PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
                .where(post.memberId.eq(memberId))
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset())
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public Optional<PostResponseDetail> getPostWithDetails(Long postId) {
        List<Tuple> results = jpaQueryFactory
                .select(post, channel, question)
                .from(post)
                .leftJoin(channel).on(post.channelId.eq(channel.id))
                .leftJoin(question).on(question.post.id.eq(post.id))
                .where(post.id.eq(postId))
                .fetch();

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Post fetchedPost = results.get(0).get(post);
        Channel fetchedChannel = results.get(0).get(channel);

        List<QuestionResponse> questionResponses = results.stream()
                .map(tuple -> tuple.get(question))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Question::getStartTime))
                .map(QuestionResponse::new)
                .collect(Collectors.toList());

        PostResponse postResponse = new PostResponse(fetchedPost);
        ChannelResponse channelResponse = new ChannelResponse(fetchedChannel);

        PostResponseDetail postResponseDetail = new PostResponseDetail(postResponse, questionResponses, channelResponse);
        return Optional.of(postResponseDetail);
    }
}
