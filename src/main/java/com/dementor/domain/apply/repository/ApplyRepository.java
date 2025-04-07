package com.dementor.domain.apply.repository;

import com.dementor.domain.apply.entity.Apply;
import com.dementor.domain.apply.entity.ApplyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

	Page<Apply> findByMemberId(Long memberId, Pageable pageable);

	Page<Apply> findByMentoringClassIdIn(List<Long> classId, Pageable pageable);

	// 대기 중인 요청 수 계산
	Integer countByMentoringClassIdInAndStatus(List<Long> mentoringClassIds, ApplyStatus status);

	// 완료된 세션 수 계산 - 오늘 날짜보다 이전인 세션 카운트
	@Query("SELECT COUNT(a) FROM Apply a WHERE a.mentoringClass.id IN :classIds " +
			"AND a.applyStatus = 'APPROVED' AND a.schedule < CURRENT_DATE")
	Integer countCompletedSessions(@Param("classIds") List<Long> classIds);
}
