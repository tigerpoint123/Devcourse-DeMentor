package com.dementor.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dementor.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
