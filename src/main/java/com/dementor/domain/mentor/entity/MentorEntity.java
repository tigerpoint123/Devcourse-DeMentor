package com.dementor.domain.mentor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mentor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MentorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Column(name = "career", nullable = false)
    private Integer career;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "introduction", nullable = false)
    private String introduction;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @Column(name = "is_modified", nullable = false)
    private Boolean isModified;

    // User 엔티티와의 관계

    // Job 엔티티와의 관계

    // PostAttachment 엔티티와의 관계

    public MentorEntity updateInfo(Long jobId, String name, Integer career,
                                   String phone, String introduction) {
        return this.toBuilder()
                .jobId(jobId != null ? jobId : this.jobId)
                .name(name != null ? name : this.name)
                .career(career != null ? career : this.career)
                .phone(phone != null ? phone : this.phone)
                .introduction(introduction != null ? introduction : this.introduction)
                .isModified(true)
                .build();
    }

    // 승인 상태 변경 메서드
    public MentorEntity updateApproval(Boolean isApproved) {
        return this.toBuilder()
                .isApproved(isApproved)
                .build();
    }
}
