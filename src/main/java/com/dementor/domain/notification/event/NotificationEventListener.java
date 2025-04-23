package com.dementor.domain.notification.event;

import com.dementor.domain.apply.event.MentoringApplyEvent;
import com.dementor.domain.notification.dto.request.NotificationRequest;
import com.dementor.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final NotificationService notificationService;

    @Async
    @Transactional
    @EventListener(SessionConnectEvent.class)
    public void handleNotificationEvent(MentoringApplyEvent event) {
        NotificationEvent notificationEvent = event.toNotificationEvent();

        try {
            NotificationRequest request = NotificationRequest.of(
                    notificationEvent.getType(),
                    notificationEvent.getMessageParams().get("content"),
                    notificationEvent.getData()
            );

            notificationService.sendNotification(notificationEvent.getReceiverId(), request);
        } catch (Exception e) {
            log.error("Failed to process notification event", e);
            // 실패 처리 로직
        }
    }
}
