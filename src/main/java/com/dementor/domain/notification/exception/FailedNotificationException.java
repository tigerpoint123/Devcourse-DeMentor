package com.dementor.domain.notification.exception;

import com.dementor.domain.notification.dto.request.NotificationRequest;
import lombok.Getter;

@Getter
public class FailedNotificationException extends RuntimeException {
    private final NotificationRequest failedRequest;

    public FailedNotificationException(Throwable cause, NotificationRequest failedRequest) {
        super(cause.getMessage(), cause);
        this.failedRequest = failedRequest;
    }
}
