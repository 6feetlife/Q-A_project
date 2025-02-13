package com.springboot.member.repository;

import com.springboot.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(long userId);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
}
