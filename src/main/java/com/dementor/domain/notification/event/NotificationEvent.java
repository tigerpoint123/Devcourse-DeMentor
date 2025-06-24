package com.dementor.domain.notification.event;

import com.dementor.domain.notification.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationEvent {
    private final NotificationRequest notificationRequest;

    public NotificationRequest getNotificationRequest() {
        return notificationRequest;
    }
}
