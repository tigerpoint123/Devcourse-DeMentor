package com.dementor.domain.mentor.entity;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import com.dementor.domain.postattachment.entity.PostAttachment;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "mentor")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mentor {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // member_id를 PK이자 FK로 사용
    @JoinColumn
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Job job;

    // PostAttachment 엔티티와의 관계 (일대다)
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAttachment> attachments;

    // Mentoring 수업 엔티티와의 관계 (일대다)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MentoringClass> mentorings;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(length = 20)
    private String currentCompany;

    @Column(nullable = false)
    private Integer career;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 255, nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(length = 255)
    private String bestFor;

    @Column(length = 255)
    private String stack;

    // 승인 상태 Enum
    public enum ApprovalStatus {
        PENDING,    // 대기 중
        APPROVED,   // 승인됨
        REJECTED    // 거부됨
    }

    // 승인 상태 변경 메서드
    public void updateApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    // 필드 수정 메서드
    public void update(String currentCompany, Integer career, String phone,
                       String introduction, String bestFor, String stack) {
        this.currentCompany = currentCompany;
        this.career = career;
        this.phone = phone;
        this.introduction = introduction;
        this.bestFor = bestFor;
        this.stack = stack;
    }
}
