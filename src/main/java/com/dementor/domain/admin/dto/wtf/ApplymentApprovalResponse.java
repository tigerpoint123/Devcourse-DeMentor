package com.dementor.domain.admin.dto.wtf;

import com.dementor.domain.admin.entity.AdminMentorApplyment;
import com.dementor.domain.mentor.entity.Mentor;

public record ApplymentApprovalResponse(
        Long id,
        Long memberId,
        Long mentorId,
        String name,
        String status,
        String modifiedAt
) {
    public static ApplymentApprovalResponse from(AdminMentorApplyment applyment, Mentor mentor) {
        return new ApplymentApprovalResponse(
                applyment.getId(),
                applyment.getMemberId(),
                mentor.getId(),
                applyment.getName(),
                applyment.getStatus().name(),
                applyment.getModifiedAt().toString()
        );
    }
}
