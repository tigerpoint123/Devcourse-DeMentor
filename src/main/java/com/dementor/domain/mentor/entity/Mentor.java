package com.dementor.domain.mentor.entity;

import com.dementor.domain.job.entity.Job;
import com.dementor.domain.member.entity.Member;
import com.dementor.domain.mentoringclass.entity.MentoringClass;
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
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // member_id를 PK이자 FK로 사용
    @JoinColumn
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Job job;

    // PostAttachment 엔티티와의 관계 (일대일, 식별관계)
    //todo: 파일첨부 테이블과 연관관계
    //private PostAttachment attachment;

    // Mentoring 수업 엔티티와의 관계 (일대다)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MentoringClass> mentorings;

    // todo: 파일첨부 테이블과의 관계 (추후 구현)
    //@Column(name = "attachment_id")
    //private Long attachmentId;

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

    @Setter
    @Column(nullable = false)
    private Boolean isApproved;

    @Setter
    @Column(nullable = false)
    private Boolean isModified;

    @Column(length = 255)
    private String bestFor;

    // 멘토링 수업 추가 메서드
    public void addMentoringClass(MentoringClass mentoringClass) {
        mentorings.add(mentoringClass);
    }
}
