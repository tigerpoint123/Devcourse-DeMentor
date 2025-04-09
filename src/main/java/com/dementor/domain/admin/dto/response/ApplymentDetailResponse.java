package com.dementor.domain.admin.dto.response;

import com.dementor.domain.mentor.entity.MentorApplication;

public record ApplymentDetailResponse(
        ApplymentInfo applymentInfo
) {
    public static ApplymentDetailResponse from(
            MentorApplication applyment
//            Job job
//            List<PostAttachment> attachments
    ) {
        return new ApplymentDetailResponse(
                ApplymentInfo.from(applyment)
        );
    }
}
