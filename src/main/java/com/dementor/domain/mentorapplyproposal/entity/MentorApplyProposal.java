package com.dementor.domain.mentorapplyproposal.entity;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.global.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "mentor_apply_proposal")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorApplyProposal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorApplyProposalStatus status;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(name = "current_company", length = 20)
    private String currentCompany;

    @Column(nullable = false)
    private Integer career;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String introduction;


    // 지원 상태 업데이트
    public void updateStatus(MentorApplyProposalStatus status) {
        this.status = status;
    }

    // 멘토 엔티티로 변환
    public Mentor toMentor() {
        if (this.status != MentorApplyProposalStatus.APPROVED) {
            throw new IllegalStateException("승인되지 않은 지원은 멘토로 변환할 수 없습니다.");
        }

        return Mentor.builder()
                .member(this.member)
                .job(this.job)
                .name(this.name)
                .career(this.career)
                .phone(this.phone)
                .email(this.email)
                .currentCompany(this.currentCompany)
                .introduction(this.introduction)
                .build();
    }
}
