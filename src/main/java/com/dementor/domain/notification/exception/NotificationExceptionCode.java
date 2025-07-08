package com.dementor.domain.notification.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum NotificationExceptionCode {
    // 알림 관련 에러 코드 추가
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다."),
    NOTIFICATION_SEND_FAILED(HttpStatus.FORBIDDEN, "알림 전송에 실패했습니다."),
    NOTIFICATION_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "알림에 대한 접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

}
