package com.dementor.domain.admin.dto.wtf;

import com.dementor.domain.admin.entity.AdminMentorApplyment;
import com.dementor.domain.job.entity.Job;

public record ApplymentResponse(
        Long id,
        Long memberId,
        String name,
        String email,
        String jobName,
        Integer career,
        String status,
        String createdAt
){
    public static ApplymentResponse from(AdminMentorApplyment applyment, Job job) {
        return new ApplymentResponse(
                applyment.getId(),
                applyment.getMemberId(),
                applyment.getName(),
                applyment.getEmail(),
                job.getName(),
                applyment.getCareer(),
                applyment.getStatus().name(),
                applyment.getCreatedAt().toString()
        );
    }
}
