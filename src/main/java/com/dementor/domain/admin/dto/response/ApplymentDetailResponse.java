package com.dementor.domain.admin.dto.response;

import com.dementor.domain.mentorapplyproposal.entity.MentorApplyProposal;

public record ApplymentDetailResponse(
        ApplymentInfo applymentInfo
) {
    public static ApplymentDetailResponse from(
            MentorApplyProposal applyment
//            Job job
//            List<PostAttachment> attachments
    ) {
        return new ApplymentDetailResponse(
                ApplymentInfo.from(applyment)
        );
    }
}
