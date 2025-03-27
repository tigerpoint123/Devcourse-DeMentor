package com.dementor.domain.mentor.entity;

import com.dementor.domain.categories.entity.Categories;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mentor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    // Member 엔티티와의 관계 (일대일)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // categories 엔티티와의 관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Categories category;

    // PostAttachment 엔티티와의 관계 (일대일, 식별관계)
    //todo: 파일첨부 테이블과 연관관계
    //private PostAttachment attachment;

    // Mentoring 수업 엔티티와의 관계 (일대다)
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "mentor_id")
    private List<MentoringClass> mentorings = new ArrayList<>();

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Column(name = "current_company", length = 20)
    private String currentCompany;

    @Column(name = "career", nullable = false)
    private Integer career;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "introduction", length = 255, nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_approved", nullable = false)
    private ApprovalStatus isApproved;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_modified", nullable = false)
    private ApprovalStatus isModified;

    @Column(name = "best_for", length = 255)
    private String bestFor;

    @Column(name = "attachment_id")
    private Long attachmentId;

    // ApprovalStatus Enum 정의
    public enum ApprovalStatus {
        Y, N
    }

    public Mentor updateInfo(Categories category, String name, Integer career,
                             String phone, String introduction) {
        return this.toBuilder()
                .category(category != null ? category : this.category)
                .name(name != null ? name : this.name)
                .career(career != null ? career : this.career)
                .phone(phone != null ? phone : this.phone)
                .introduction(introduction != null ? introduction : this.introduction)
                .isModified(ApprovalStatus.Y)
                .build();
    }

    // 승인 상태 변경 메서드
    public Mentor updateApproval(ApprovalStatus isApproved) {
        return this.toBuilder()
                .isApproved(isApproved)
                .build();
    }

    // 멘토링 수업 추가 메서드
    public void addMentoringClass(MentoringClass mentoringClass) {
        mentorings.add(mentoringClass);
    }
}
