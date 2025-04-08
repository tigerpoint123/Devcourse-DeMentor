package com.dementor.domain.admin.dto.wtf;

import com.dementor.domain.admin.entity.AdminMentorApplyment;
import com.dementor.domain.job.entity.Job;

public record ApplymentDetailResponse(
        ApplymentInfo applymentInfo
) {
    public static ApplymentDetailResponse from(
            AdminMentorApplyment applyment,
            Job job
//            List<PostAttachment> attachments
    ) {
        return new ApplymentDetailResponse(
                ApplymentInfo.from(applyment, job)
        );
    }
}
