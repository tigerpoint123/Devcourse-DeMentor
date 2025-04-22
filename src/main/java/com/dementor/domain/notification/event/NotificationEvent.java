package com.dementor.domain.notification.event;

import com.dementor.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public class NotificationEvent {
    private final Long receiverId;
    private final NotificationType type;
    private final Map<String, String> messageParams;
    private final Map<String, Object> data;
}
