package com.dementor.domain.admin.dto.response;

import com.dementor.domain.mentor.entity.Mentor;
import com.dementor.domain.mentor.entity.MentorApplication;

public record ApplymentApprovalResponse(
        Long id,
        Long memberId,
        Long mentorId,
        String name,
        String status,
        String modifiedAt
) {
    public static ApplymentApprovalResponse from(MentorApplication applyment, Mentor mentor) {
        return new ApplymentApprovalResponse(
                applyment.getId(),
                applyment.getMember().getId(),
                mentor.getId(),
                applyment.getName(),
                applyment.getStatus().name(),
                applyment.getModifiedAt().toString()
        );
    }
}
