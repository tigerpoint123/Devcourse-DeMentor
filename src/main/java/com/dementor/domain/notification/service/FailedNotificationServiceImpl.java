package com.dementor.domain.notification.service;

import com.dementor.domain.notification.dto.request.NotificationRequest;
import com.dementor.domain.notification.dto.response.FailedNotificationResponse;
import com.dementor.domain.notification.entity.ErrorType;
import com.dementor.domain.notification.entity.FailedNotification;
import com.dementor.domain.notification.repository.FailedNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FailedNotificationServiceImpl implements FailedNotificationService {
    private final FailedNotificationRepository failedNotificationRepository;
    private static final int MAX_RETRY = 5;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public FailedNotificationResponse manualRetriedFailedNotification(Long id) {
        // 실패 알림 중 retried=false인 것만 조회
        // 알림 발송 재시도 후 성공 시 retried=true, retriedAt=now()로 업데이트
        // @Scheduled 어노테이션을 사용해 일정 주기로 재처리 메서드 실행
        FailedNotification failedNotification = failedNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("실패 알림 조회 실패"));

        if (failedNotification.isRetried())
            return new FailedNotificationResponse(
                    false,
                    "이미 재처리된 알림",
                    id,
                    null
            );
        if (failedNotification.getRetryCount() >= MAX_RETRY)
            return new FailedNotificationResponse(
                    false,
                    "재시도 횟수 초과",
                    id,
                    null
            );

        failedNotification.increaseRetryCount(); // retryCount 증가
        try {
            notificationService.sendApplymentNotification(
                    failedNotification.getReceiver().getId(),
                    new NotificationRequest(
                            failedNotification.getType(),
                            failedNotification.getContent(),
                            failedNotification.getData()
                    )
            );
            // 성공 시
            failedNotification.markRetried();
            failedNotificationRepository.save(failedNotification);
            return new FailedNotificationResponse(
                    true,
                    "재처리 성공",
                    id,
                    null
            );
        } catch (Exception e) {
            // 실패 시
            ErrorType errorType = ErrorType.UNKNOWN;
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (msg.contains("timeout")) errorType = ErrorType.TIMEOUT;
            else if (msg.contains("network")) errorType = ErrorType.NETWORK;
            failedNotification.updateError(e.getMessage(), errorType);

            failedNotificationRepository.save(failedNotification);
            return new FailedNotificationResponse(
                    false,
                    "재처리 실패",
                    id,
                    e.getMessage()
            );
        }

    }

    @Override
    @Scheduled(fixedDelay = 300000) // 5분마다 실행
    public void autoRetriedFailedNotification() {
        //findAll() 대신 retried=false, retryCount < MAX_RETRY 조건으로 자동 재처리 대상을 추출
        List<FailedNotification> failedNotificationList = failedNotificationRepository.findAll();
    }
}
