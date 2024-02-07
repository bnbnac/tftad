package com.tftad.repository;

import com.tftad.domain.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
