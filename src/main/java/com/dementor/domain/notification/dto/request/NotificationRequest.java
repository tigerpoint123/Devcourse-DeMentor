package com.dementor.domain.notification.dto.request;

import com.dementor.domain.notification.entity.NotificationType;

import java.io.Serializable;
import java.util.Map;

public record NotificationRequest(
    NotificationType type,
    String content,
    Map<String, Object> data
) implements Serializable {
    public static NotificationRequest of(
            NotificationType type,
            String content,
            Map<String, Object> data
    ) {
        return new NotificationRequest(type, content, data);
    }
}
