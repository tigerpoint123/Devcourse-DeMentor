package com.dementor.domain.mentor.repository;

import com.dementor.domain.mentor.entity.MentorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<MentorEntity, Long> {
}
