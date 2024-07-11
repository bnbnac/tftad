package com.tftad.repository;

import com.tftad.response.MemberResponseDetail;

public interface MemberRepositoryCustom {
    MemberResponseDetail getMemberWithDetails(Long memberId);
}
