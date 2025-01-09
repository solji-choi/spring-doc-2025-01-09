package com.ll.spring_doc_2025_01_09.domain.member.member.repository;

import com.ll.spring_doc_2025_01_09.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByApiKey(String apiKey);
}