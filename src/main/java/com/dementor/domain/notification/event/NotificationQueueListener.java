package com.dementor.domain.notification.event;

import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.exception.MemberErrorCode;
import com.dementor.domain.member.exception.MemberException;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.notification.dto.request.NotificationRequest;
import com.dementor.domain.notification.entity.FailedNotification;
import com.dementor.domain.notification.exception.FailedNotificationException;
import com.dementor.domain.notification.repository.FailedNotificationRepository;
import com.dementor.domain.notification.service.NotificationService;
import com.dementor.global.config.NotificationRabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationQueueListener {
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final FailedNotificationRepository failedNotificationRepository;

    @RabbitListener (queues = NotificationRabbitMqConfig.NOTIFICATION_QUEUE)
    public void receiveNotification(NotificationRequest request) throws Exception {
        log.info("Received notification request: {}", request);

        try {
            Long memberId = extractMemberId(request.data().get("memberId"));
            Long mentorId = extractMemberId(request.data().get("mentorId"));
            if (memberId == null) log.error("memberId is null");
            if (mentorId == null) log.error("mentorId is null");

            notificationService.sendApplymentNotification(memberId, request);
            notificationService.sendApplymentNotification(mentorId, request);
            log.info("Notification processed successfully for memberId: {}", memberId);
        } catch (Exception e) {
            log.error("Failed to process notification: {}", request, e);
            Map<String, Object> dataWithError = new HashMap<>(request.data());
            dataWithError.put("errorMessage", e.getMessage());

            NotificationRequest failedRequest = NotificationRequest.of(
                    request.type(),
                    request.content(),
                    dataWithError
            );
            throw new FailedNotificationException(e, failedRequest);
        }
    }

    @RabbitListener(queues = NotificationRabbitMqConfig.NOTIFICATION_DLQ)
    public void handleDLQ(NotificationRequest request) {
        log.error("DLQ로 이동한 알림 메시지 : {}", request);

        Long memberId = extractMemberId(request.data().get("memberId"));
        Long mentorId = extractMemberId(request.data().get("mentorId"));

        saveFailedNotification(memberId, request);
        saveFailedNotification(mentorId, request);
    }

    private Long extractMemberId(Object object) {
        try {
            if (object instanceof Long) {
                return (Long) object;
            } else if (object instanceof Integer) {
                return ((Integer) object).longValue();
            } else if (object instanceof String) {
                return Long.parseLong((String) object);
            }
            log.error("Unknown memberId type: {}", object.getClass());
            return null;
        } catch (Exception e) {
            log.error("Failed to extract memberId from object: {}", object, e);
            return null;
        }
    }

    private void saveFailedNotification(Long memberId, NotificationRequest request) {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        FailedNotification failed = FailedNotification.builder()
                .receiver(receiver)
                .type(request.type())
                .content(request.content())
                .data(request.data())
                .errorMessage(request.data().get("errorMessage").toString())
                .retried(false)
                .build();
        failedNotificationRepository.save(failed);
    }
}
