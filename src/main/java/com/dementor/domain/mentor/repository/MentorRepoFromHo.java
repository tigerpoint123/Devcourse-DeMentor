package com.dementor.domain.mentor.repository;

import com.dementor.domain.mentor.dto.response.MyMentoringResponse;
import com.dementor.domain.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepoFromHo extends JpaRepository<Mentor, Long> {

    MyMentoringResponse findByMemerId(Long menberId);
}
