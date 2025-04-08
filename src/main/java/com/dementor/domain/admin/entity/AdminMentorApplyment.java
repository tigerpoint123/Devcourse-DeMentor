package com.dementor.domain.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class AdminMentorApplyment {
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

    private int memberId;

    private int jobId;
}
