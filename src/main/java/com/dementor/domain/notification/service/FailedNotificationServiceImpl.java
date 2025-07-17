package com.dementor.domain.notification.service;

import com.dementor.domain.notification.repository.FailedNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FailedNotificationServiceImpl implements FailedNotificationService{
    private final FailedNotificationRepository failedNotificationRepository;

    @Override
    @Scheduled(fixedDelay = 300000) // 5분마다 실행
    public void retriedFailedNotification() {
        // 실패 알림 중 retried=false인 것만 조회
        // 알림 발송 재시도 후 성공 시 retried=true, retriedAt=now()로 업데이트
        // @Scheduled 어노테이션을 사용해 일정 주기로 재처리 메서드 실행

    }
}
