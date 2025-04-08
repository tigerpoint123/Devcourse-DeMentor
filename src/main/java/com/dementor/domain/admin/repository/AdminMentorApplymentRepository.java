package com.dementor.domain.admin.repository;

import com.dementor.domain.admin.entity.AdminMentorApplyment;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminMentorApplymentRepository extends JpaRepository<AdminMentorApplyment, Long> {
    @Query("SELECT a, j FROM AdminMentorApplyment a " +
            "LEFT JOIN Job j ON a.jobId = j.id " +
            "ORDER BY a.createdAt DESC")
    Page<Tuple> findAllWithJob(Pageable pageable);
}
