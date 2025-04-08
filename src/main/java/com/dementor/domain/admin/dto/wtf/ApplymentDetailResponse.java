package com.dementor.domain.admin.dto.wtf;

import com.dementor.domain.admin.entity.AdminMentorApplyment;
import com.dementor.domain.job.entity.Job;
import com.dementor.domain.postattachment.entity.PostAttachment;

import java.util.List;

public record ApplymentDetailResponse(
        ApplymentInfo applymentInfo
) {
    public static ApplymentDetailResponse from(
            AdminMentorApplyment applyment,
            Job job,
            List<PostAttachment> attachments
    ) {
        return new ApplymentDetailResponse(
                ApplymentInfo.from(applyment, job, attachments)
        );
    }
}
