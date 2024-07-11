package com.tftad.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tftad.domain.Member;
import com.tftad.response.ChannelResponse;
import com.tftad.response.MemberResponse;
import com.tftad.response.MemberResponseDetail;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.tftad.domain.QChannel.channel;
import static com.tftad.domain.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MemberResponseDetail getMemberWithDetails(Long memberId) {
        List<Tuple> results = jpaQueryFactory
                .select(member, channel)
                .from(member)
                .leftJoin(channel).on(channel.memberId.eq(member.id))
                .where(member.id.eq(memberId))
                .fetch();

        if (results.isEmpty()) {
            return null;
        }

        Member fetchMember = results.get(0).get(member);

        MemberResponse memberResponse = new MemberResponse(fetchMember);

        List<ChannelResponse> channelResponses = results.stream()
                .map(tuple -> tuple.get(channel))
                .filter(Objects::nonNull)
                .map(ChannelResponse::new)
                .collect(Collectors.toList());


        return new MemberResponseDetail(memberResponse, channelResponses);
    }
}
