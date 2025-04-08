package com.dementor.domain.admin.dto;

import com.dementor.domain.admin.entity.AdminMentorApplyment;
import com.dementor.domain.admin.entity.Status;

public record ApplymentResponse(
        Long id,                // 지원서 ID
        String name,            // 지원자 이름
        String currentCompany,  // 현재 회사
        Integer career,         // 경력
        String phone,           // 연락처
        String email,           // 이메일
        String introduction,    // 자기소개
        String bestFor,         // 추천 대상
        Status status,          // 지원 상태
        int memberId,          // 회원 ID
        int jobId              // 직무 ID
){
    public static ApplymentResponse from(AdminMentorApplyment applyment) {
        return new ApplymentResponse(
                applyment.getId(),
                applyment.getName(),
                applyment.getCurrentCompany(),
                applyment.getCareer(),
                applyment.getPhone(),
                applyment.getEmail(),
                applyment.getIntroduction(),
                applyment.getBestFor(),
                applyment.getStatus(),
                applyment.getMemberId(),
                applyment.getJobId()
        );
    }
}
