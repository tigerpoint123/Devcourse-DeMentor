package com.dementor.domain.notification.event;

import com.dementor.domain.apply.event.MentoringApplyEvent;
import com.dementor.domain.notification.dto.request.NotificationRequest;
import com.dementor.domain.notification.entity.NotificationType;
import com.dementor.global.config.NotificationRabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        rabbitTemplate.convertAndSend(
                NotificationRabbitMqConfig.NOTIFICATION_EXCHANGE,
                NotificationRabbitMqConfig.NOTIFICATION_ROUTING_KEY,
                event.getNotificationRequest()
        );
    }

    @EventListener
    public void handleMentoringApplyEvent(MentoringApplyEvent event) {
        log.info("Mentoring apply event received : applyId={}, classId={}, mentorId={},memberId={}",
                event.applyId(), event.mentoringClassId(), event.mentorId(),event.memberId());

        NotificationRequest request = NotificationRequest.of(
                NotificationType.MENTORING_APPLY,
                "멘토링 신청이 접수되었습니다.",
                Map.of(
                        "applyId", event.applyId(),
                        "classId", event.mentoringClassId(),
                        "mentorId", event.mentorId(),
                        "memberId", event.memberId()
                )
        );

        CorrelationData correlationData = new CorrelationData(
                "notification-" + event.applyId() + "-" + event.memberId() + System.currentTimeMillis()
        );

        // RabbitMQ 에 알림 메시지 전송 + confirm
        rabbitTemplate.convertAndSend(
                NotificationRabbitMqConfig.NOTIFICATION_EXCHANGE,
                NotificationRabbitMqConfig.NOTIFICATION_ROUTING_KEY,
                request,
                correlationData
        );
        log.info("Notification request sent to RabbitMQ for memberId: {}", event.memberId());
    }
}
