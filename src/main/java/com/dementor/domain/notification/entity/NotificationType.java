package com.dementor.domain.notification.entity;

import lombok.Getter;

@Getter
public enum NotificationType { // TODO : 멘토링 관련, 멘토 관련 등의 enum 추가
    MENTORING_REQUEST("새로운 멘토링 신청"),
    MENTOR_APPROVED("멘토 승인");
//    MENTORING_CANCELED("멘토링 취소"),
//    NEW_MESSAGE("새로운 메시지");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

}
