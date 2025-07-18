package com.dementor.domain.notification.dto.response;

public record FailedNotificationResponse(
        boolean success,
        String message,
        Long notificationId,
        String errorMessage
) {
}
