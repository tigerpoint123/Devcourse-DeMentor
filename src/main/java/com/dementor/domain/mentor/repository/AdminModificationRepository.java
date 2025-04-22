package com.dementor.domain.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dementor.domain.mentor.entity.MentorEditProposal;

public interface AdminModificationRepository extends JpaRepository<MentorEditProposal, Long> {
}
