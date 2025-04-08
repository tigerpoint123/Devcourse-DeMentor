package com.dementor.domain.admin.entity;

import com.dementor.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class AdminMentorApplyment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    private String name;

    private String currentCompany;

    private Integer career;

    private String phone;

    private String email;

    private String introduction;

    private String bestFor; // 추천 댓상

    @Enumerated(EnumType.STRING)
    private Status status;

    private Long memberId;

    private Long jobId;

    public void updateStatus(Status status) {
        this.status = status;
    }
}
