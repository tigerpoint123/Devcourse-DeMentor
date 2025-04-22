package com.dementor.domain.notification.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationExceptionCode {
    // 알림 관련 에러 코드 추가
    NOTIFICATION_NOT_FOUND("NOT001", "알림을 찾을 수 없습니다."),
    NOTIFICATION_SEND_FAILED("NOT002", "알림 전송에 실패했습니다."),
    NOTIFICATION_ACCESS_DENIED("NOT003", "알림에 대한 접근 권한이 없습니다.");

    private final String code;
    private final String message;

}
