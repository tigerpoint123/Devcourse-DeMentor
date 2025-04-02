package com.dementor.domain.mentor.service;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.job.repository.JobRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.dto.request.MentorApplicationRequest;
import com.dementor.domain.mentor.dto.request.MentorChangeRequest;
import com.dementor.domain.mentor.dto.request.MentorUpdateRequest;
import com.dementor.domain.mentor.dto.response.MentorChangeResponse;
import com.dementor.domain.mentor.dto.response.MentorInfoResponse;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.postattachment.repository.PostAttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {
    private final MentorRepository mentorRepository;
    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;
    private final PostAttachmentRepository attachmentRepository;

    //멘토 지원하기
    @Transactional
    public void applyMentor(MentorApplicationRequest.MentorApplicationRequestDto requestDto) {
        // 회원 엔티티 조회
        Member member = memberRepository.findById(requestDto.memberId())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다: " + requestDto.memberId()));

        // 회원의 역할이 이미 MENTOR인지 확인
        if (member.getUserRole() == UserRole.MENTOR) {
            throw new IllegalStateException("이미 멘토로 등록된 사용자입니다: " + requestDto.memberId());
        }

        // 직무 엔티티 조회
        Job job = jobRepository.findById(requestDto.jobId())
                .orElseThrow(() -> new EntityNotFoundException("직무를 찾을 수 없습니다: " + requestDto.jobId()));

        // 멘토 엔티티 생성 - 초기 상태는 PENDING
        Mentor mentor = requestDto.toEntity(member, job);

        // 첨부파일 연결 - TODO: 파일 처리 로직 구현 필요

        // 멘토 저장 (PENDING 상태로)
        mentorRepository.save(mentor);

        // 중요: 여기서 회원 역할을 MENTOR로 변경하지 않음
        // 관리자 승인 후에 변경해야 함

    }

    //멘토 정보 업데이트
    @Transactional
    public void updateMentor(Long memberId, MentorUpdateRequest.MentorUpdateRequestDto requestDto) {
        Mentor mentor = mentorRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멘토를 찾을 수 없습니다: " + memberId));

        // 승인된 멘토만 정보 수정 요청 가능
        if (mentor.getApprovalStatus() != Mentor.ApprovalStatus.APPROVED) {
            throw new IllegalStateException("승인되지 않은 멘토는 정보를 수정할 수 없습니다: " + memberId);
        }

        // 현재 정보 수정 요청 중인지 확인
        if (mentor.getModificationStatus() == Mentor.ModificationStatus.PENDING) {
            throw new IllegalStateException("이미 정보 수정 요청 중입니다: " + memberId);
        }

        // 멘토 정보 업데이트 (임시 저장)
        requestDto.updateMentor(mentor);

        // 첨부파일 업데이트 - TODO: 파일 처리 로직 구현 필요
    }

    //멘토 정보 조회
    public MentorInfoResponse getMentorInfo(Long memberId) {
        Mentor mentor = mentorRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 멘토를 찾을 수 없습니다: " + memberId));

        // 승인된 멘토만 정보 조회 가능하도록 체크
        if (mentor.getApprovalStatus() != Mentor.ApprovalStatus.APPROVED) {
            throw new IllegalStateException("승인되지 않은 멘토 정보는 조회할 수 없습니다: " + memberId);
        }

        // 멘토링 클래스 통계 계산
        Integer totalClasses = mentor.getMentorings() != null ? mentor.getMentorings().size() : 0;

        // 대기 중인 요청 및 완료된 세션 수 계산
        // TODO: 실제 비즈니스 로직에 맞게 수정 필요
        Integer pendingRequests = 0;
        Integer completedSessions = 0;

        return MentorInfoResponse.from(mentor, totalClasses, pendingRequests, completedSessions);
    }

    //멘토 정보 수정 요청 목록 조회
    public MentorChangeResponse.ChangeListResponse getModificationRequests(
            Long memberId,
            MentorChangeRequest.ModificationRequestParams params) {
        // 멘토 존재 여부 확인
        if (!mentorRepository.existsById(memberId)) {
            throw new EntityNotFoundException("해당 멘토를 찾을 수 없습니다: " + memberId);
        }

        // 실제 DB에서 별도로 저장된 수정 요청이 없으므로, 빈 목록 반환
        List<MentorChangeResponse.ChangeRequestData> emptyList = new ArrayList<>();
        MentorChangeResponse.Pagination pagination = new MentorChangeResponse.Pagination(
                params.page(),
                params.size(),
                0L // 총 요소 수 0
        );

        return new MentorChangeResponse.ChangeListResponse(emptyList, pagination);
    }
}