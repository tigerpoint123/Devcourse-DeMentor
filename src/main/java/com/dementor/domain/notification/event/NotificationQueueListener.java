package com.dementor.domain.notification.event;

import com.dementor.domain.notification.dto.request.NotificationRequest;
import com.dementor.domain.notification.service.NotificationService;
import com.dementor.global.config.NotificationRabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationQueueListener {
    private final NotificationService notificationService;

    @RabbitListener (queues = NotificationRabbitMqConfig.NOTIFICATION_QUEUE)
    public void receiveNotification(NotificationRequest request)  {
        log.info("Received notification request: {}", request);

        try {
            Long memberId = extractMemberId(request);
            if (memberId == null) log.error("memberId is null");

            notificationService.sendNotification(memberId, request);
            log.info("Notification processed successfully for memberId: {}", memberId);
        } catch (Exception e) {
            log.error("Failed to process notification: {}", request, e);
        }
//        throw new RuntimeException("DLQ 테스트용 예외");
    }

    @RabbitListener(queues = NotificationRabbitMqConfig.NOTIFICATION_DLQ)
    public void handleDLQ(NotificationRequest request) {

    }

    private Long extractMemberId(NotificationRequest request) {
        try {
            Object memberIdObj = request.data().get("memberId");
            if (memberIdObj instanceof Long) {
                return (Long) memberIdObj;
            } else if (memberIdObj instanceof Integer) {
                return ((Integer) memberIdObj).longValue();
            } else if (memberIdObj instanceof String) {
                return Long.parseLong((String) memberIdObj);
            }
            log.error("Unknown memberId type: {}", memberIdObj.getClass());
            return null;
        } catch (Exception e) {
            log.error("Failed to extract memberId from request: {}", request, e);
            return null;
        }
    }
}
