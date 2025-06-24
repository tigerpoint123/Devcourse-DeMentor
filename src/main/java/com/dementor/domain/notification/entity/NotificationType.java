package com.dementor.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
    MENTORING_APPLY("새로운 멘토링 신청"),
    MENTORING_APPLY_ACCEPTED("멘토링 신청 승인");
//    MENTORING_CANCELED("멘토링 취소"),
//    NEW_MESSAGE("새로운 메시지");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

}
