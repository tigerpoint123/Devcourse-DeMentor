package com.dementor.domain.admin;

import com.dementor.domain.admin.dto.wtf.*;
import com.dementor.domain.job.entity.Job;
import com.dementor.domain.job.repository.JobRepository;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.entity.MentorApplication;
import com.dementor.domain.mentor.repository.MentorApplicationRepository;
import com.dementor.domain.mentor.repository.MentorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.dementor.domain.mentor.entity.MentorApplication.ApplicationStatus.APPROVED;
import static com.dementor.domain.mentor.entity.MentorApplication.ApplicationStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class AdminMentorApplymentService {
    private final JobRepository jobRepository;
    private final MentorRepository mentorRepository;
    private final MemberRepository memberRepository;
    private final MentorApplicationRepository mentorApplicationRepository;

    public Page<ApplymentResponse> findAllApplyment(Pageable pageable) {
        return mentorApplicationRepository.findAll(pageable)
                .map(application -> ApplymentResponse.from(application, application.getJob()));
    }

    public ApplymentDetailResponse findOneApplyment(Long memberId) {
        // 지원서 조회
        MentorApplication applyment = mentorApplicationRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));

        // 첨부파일 목록 조회
//        List<PostAttachment> attachments = attachmentRepository.findByApplymentId(memberId);

        return ApplymentDetailResponse.from(
                applyment
//                job
//                attachments
        );
    }

    public ApplymentApprovalResponse approveApplyment(Long memberId) {
        // 지원서 조회
        MentorApplication applyment = mentorApplicationRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));

        // 회원 정보 조회
        Member member = memberRepository.findById(applyment.getMember().getId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        // 직무 정보 조회
        Job job = jobRepository.findById(applyment.getJob().getId())
                .orElseThrow(() -> new EntityNotFoundException("직무 정보를 찾을 수 없습니다."));

        // 멘토 엔티티 생성
        Mentor mentor = Mentor.builder()
                .member(member)
                .job(job)
                .name(applyment.getName())
                .currentCompany(applyment.getCurrentCompany())
                .career(applyment.getCareer())
                .phone(applyment.getPhone())
                .email(applyment.getEmail())
                .introduction(applyment.getIntroduction())
                .bestFor(applyment.getBestFor())
                .approvalStatus(Mentor.ApprovalStatus.APPROVED)
                .build();

        mentorRepository.save(mentor);

        applyment.updateStatus(APPROVED, null);
        MentorApplication updatedApplyment = mentorApplicationRepository.save(applyment);

        return ApplymentApprovalResponse.from(updatedApplyment, mentor);
    }

    public ApplymentRejectResponse rejectApplyment(Long memberId, ApplymentRejectRequest request) {
        // 지원서 조회
        MentorApplication applyment = mentorApplicationRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));

        // 지원서 상태를 거절로 변경하고 거절 사유 저장
        applyment.updateStatus(REJECTED, request.reason());
        // 저장
        MentorApplication updatedApplyment = mentorApplicationRepository.save(applyment);

        // 회원 정보 조회
        Member member = memberRepository.findById(applyment.getMember().getId())
                .orElseThrow(() -> new EntityNotFoundException("회원 정보를 찾을 수 없습니다."));

        // Response 생성 
        return ApplymentRejectResponse.of(
                updatedApplyment,
                member,
                request.reason()
        );
    }
}
