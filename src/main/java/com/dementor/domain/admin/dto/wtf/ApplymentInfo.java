package com.dementor.domain.admin.dto.wtf;

import com.dementor.domain.admin.entity.AdminMentorApplyment;
import com.dementor.domain.job.entity.Job;

public record ApplymentInfo(
        Long applymentId,
        Long memberId,
        String name,
        JobInfo job,
        Integer career,
        String phone,
        String email,
        String currentCompany,
        String introduction,
        String bestFor,
        String status,
        String createdAt,
        String modifiedAt
//        List<AttachmentInfo> attachments
) {
    public static ApplymentInfo from(
            AdminMentorApplyment applyment,
            Job job
//            List<PostAttachment> attachments
    ) {
        return new ApplymentInfo(
                applyment.getId(),
                applyment.getMemberId(),
                applyment.getName(),
                JobInfo.from(job),
                applyment.getCareer(),
                applyment.getPhone(),
                applyment.getEmail(),
                applyment.getCurrentCompany(),
                applyment.getIntroduction(),
                applyment.getBestFor(),
                applyment.getStatus().name(),
                applyment.getCreatedAt().toString(),
                applyment.getModifiedAt() != null ? applyment.getModifiedAt().toString() : null
//                attachments.stream()
//                        .map(AttachmentInfo::from)
//                        .toList()
        );
    }
}
