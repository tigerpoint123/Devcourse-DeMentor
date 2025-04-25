package com.dementor.domain.mentor.repository;

import com.dementor.domain.mentor.entity.MentorEditProposal;
import com.dementor.domain.mentor.entity.MentorEditProposalStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MentorEditProposalRepository extends JpaRepository<MentorEditProposal, Long> {
	//특정 멘토의 정보 수정 요청 목록을 조회합니다.
	@Query("SELECT mm FROM MentorEditProposal mm WHERE mm.member.id = :memberId")
	Page<MentorEditProposal> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

	//특정 멘토의 정보 수정 요청 목록을 상태별로 필터링하여 조회합니다.
	@Query("SELECT mm FROM MentorEditProposal mm WHERE mm.member.id = :memberId AND mm.status = :status")
	Page<MentorEditProposal> findByMemberIdAndStatus(
		@Param("memberId") Long memberId,
		@Param("status") MentorEditProposalStatus status,
		Pageable pageable);

	//특정 회원(멘토)의 대기 중인 수정 요청이 있는지 확인
	@Query("SELECT COUNT(mm) > 0 FROM MentorEditProposal mm " +
		"WHERE mm.member.id = :memberId AND mm.status = 'PENDING'")
	boolean existsPendingModificationByMemberId(@Param("memberId") Long memberId);

	// 특정 멘토의 가장 최근 수정 요청 조회
	@Query("SELECT m FROM MentorEditProposal m WHERE m.member.id = :memberId ORDER BY m.createdAt DESC")
	Optional<MentorEditProposal> findLatestByMemberId(@Param("memberId") Long memberId);

	MentorEditProposal findOneRequestByMemberIdAndStatus(Long memberId,
		MentorEditProposalStatus mentorEditProposalStatus);
}
