package com.dementor.domain.mentor.service;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.job.repository.JobRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.entity.UserRole;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.dto.request.MentorApplicationRequest;
import com.dementor.domain.mentor.dto.request.MentorUpdateRequest;
import com.dementor.domain.mentor.dto.response.MentorInfoResponse;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.repository.MentorRepository;
import com.dementor.domain.postattachment.entity.PostAttachment;
import com.dementor.domain.postattachment.repository.PostAttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // 멘토 엔티티 생성
        Mentor mentor = requestDto.toEntity(member, job);

        // 지원 상태를 PENDING으로 변경
        mentor.updateApprovalStatus(Mentor.ApprovalStatus.PENDING);

        // 첨부파일 연결 - TODO: 파일 처리 로직 구현 필요
        if (requestDto.attachmentId() != null && !requestDto.attachmentId().isEmpty()) {
            List<PostAttachment> attachments = attachmentRepository.findAllById(requestDto.attachmentId());

            // 새 첨부파일마다 새 객체 생성 및 저장
            List<PostAttachment> updatedAttachments = attachments.stream()
                    .map(attachment -> PostAttachment.builder()
                            .filename(attachment.getFilename())
                            .originalFilename(attachment.getOriginalFilename())
                            .storeFilePath(attachment.getStoreFilePath())
                            .fileSize(attachment.getFileSize())
                            .member(attachment.getMember())
                            .mentor(mentor)
                            .imageType(attachment.getImageType())
                            .build())
                    .collect(Collectors.toList());

            attachmentRepository.saveAll(updatedAttachments);

            // 멘토 엔티티의 attachments 필드 설정 (필요시 초기화)
            if (mentor.getAttachments() == null) {
                mentor.updateAttachments(new ArrayList<>());
            }
            mentor.getAttachments().addAll(updatedAttachments);
        }

        // 멘토 저장
        mentorRepository.save(mentor);

    }

    //멘토 정보 업데이트
    @Transactional
    public void updateMentor(Long memberId, MentorUpdateRequest.MentorUpdateRequestDto requestDto) {
        Mentor mentor = mentorRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멘토를 찾을 수 없습니다: " + memberId));

        // 멘토 정보 업데이트
        requestDto.updateMentor(mentor);

        // 첨부파일 업데이트 - TODO: 파일 처리 로직 구현 필요
        if (requestDto.attachmentId() != null && !requestDto.attachmentId().isEmpty()) {
            // 기존 첨부파일은 그대로 두고, 새 첨부파일만 추가
            List<PostAttachment> newAttachments = attachmentRepository.findAllById(requestDto.attachmentId());

            // 새 첨부파일마다 새 객체 생성 및 저장
            List<PostAttachment> updatedAttachments = newAttachments.stream()
                    .map(attachment -> PostAttachment.builder()
                            .filename(attachment.getFilename())
                            .originalFilename(attachment.getOriginalFilename())
                            .storeFilePath(attachment.getStoreFilePath())
                            .fileSize(attachment.getFileSize())
                            .member(attachment.getMember())
                            .mentor(mentor)
                            .imageType(attachment.getImageType())
                            .build())
                    .collect(Collectors.toList());

            attachmentRepository.saveAll(updatedAttachments);
        }
    }

    //멘토 정보 조회
    public MentorInfoResponse getMentorInfo(Long memberId) {
        Mentor mentor = mentorRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 멘토를 찾을 수 없습니다: " + memberId));

        // 멘토링 클래스 통계 계산
        Integer totalClasses = mentor.getMentorings() != null ? mentor.getMentorings().size() : 0;

        // 대기 중인 요청 및 완료된 세션 수 계산
        // TODO: 실제 비즈니스 로직에 맞게 수정 필요
        Integer pendingRequests = 0;
        Integer completedSessions = 0;

        return MentorInfoResponse.from(mentor, totalClasses, pendingRequests, completedSessions);
    }
}
