package com.dementor.domain.admin.dto.wtf;

import com.dementor.domain.member.entity.Member;
import com.dementor.domain.mentor.entity.MentorApplication;

import java.time.LocalDateTime;

public record ApplymentRejectResponse(
        Long applymentId,
        Long memberId,
        String name,
        MentorApplication.ApplicationStatus status,
        LocalDateTime modifiedAt,
        String reason
) {
    // 엔티티와 요청으로부터 직접 생성하는 팩토리 메서드 추가
    public static ApplymentRejectResponse of(
            MentorApplication applyment,
            Member member,
            String rejectReason
    ) {
        return new ApplymentRejectResponse(
                applyment.getId(),
                member.getId(),
                member.getName(),
                applyment.getStatus(),
                applyment.getModifiedAt(),
                rejectReason
        );
    }
}