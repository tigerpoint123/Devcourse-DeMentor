package com.dementor.domain.notification.dto.response;

import com.dementor.domain.notification.entity.Notification;
import com.dementor.domain.notification.entity.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationResponse(
    Long id,
    NotificationType type,
    String content,
    Map<String, Object> data,
    boolean isRead,
    LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getType(),
            notification.getContent(),
            notification.getData(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
