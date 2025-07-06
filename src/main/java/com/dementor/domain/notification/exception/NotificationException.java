package com.dementor.domain.notification.exception;

import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException{
    private final NotificationExceptionCode errorCode;

    public NotificationException(NotificationExceptionCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
