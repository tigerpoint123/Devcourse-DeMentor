package com.dementor.domain.apply.event;

import com.dementor.domain.notification.entity.NotificationType;
import com.dementor.domain.notification.event.NotificationEvent;

import java.util.Map;

public record MentoringApplyEvent(
        Long applyId,
        Long mentoringClassId,
        Long mentorId,
        Long memberId,
        String className,
        String memberNickname
) {
    public NotificationEvent toNotificationEvent() {
        return NotificationEvent.builder()
                .receiverId(mentorId)
                .type(NotificationType.MENTORING_REQUEST)
                .messageParams(Map.of(
                        "content", String.format("[%s] 멘토링에 새로운 신청이 있습니다.", className)
                ))
                .data(Map.of(
                        "applicationId", applyId,
                        "mentoringClassId", mentoringClassId,
                        "studentId", memberId,
                        "studentName", memberNickname
                ))
                .build();
    }
}
