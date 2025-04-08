package com.dementor.domain.apply.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dementor.domain.apply.entity.Apply;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

	Page<Apply> findByMemberId(Long memberId, Pageable pageable);

	Page<Apply> findByMentoringClassIdIn(List<Long> classId, Pageable pageable);

	@Query("SELECT a FROM Apply a WHERE a.mentoringClass.id = :classId AND " +
	       "FUNCTION('DATE_FORMAT', a.schedule, '%Y%m%d') >= :startDate AND " +
	       "FUNCTION('DATE_FORMAT', a.schedule, '%Y%m%d') <= :endDate")
	Page<Apply> findByClassIdAndScheduleStringBetween(
		@Param("classId") Long classId,
		@Param("startDate") String startDate,
		@Param("endDate") String endDate,
		Pageable pageable
	);

}
