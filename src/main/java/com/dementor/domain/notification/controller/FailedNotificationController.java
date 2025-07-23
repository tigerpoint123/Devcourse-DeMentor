package com.dementor.domain.notification.controller;

import com.dementor.domain.notification.dto.response.FailedNotificationResponse;
import com.dementor.domain.notification.service.FailedNotificationService;
import com.dementor.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dlq/notifications")
@RequiredArgsConstructor
public class FailedNotificationController {
    /*
     단순 네트워크 오류 등 복구 가능한 에러만 자동 재처리, 그 외는 수동 처리
     무한 재시도 방지

구현해야 될 로직
1. FailedNotificationService 자동/수동 재처리 로직. retryCount 증가, errorType 분기, retried, retriedAt 갱신 등
2. 스케줄러로 자동 재처리
3. 컨트롤러에서 수동 재처리 API
4. 알림 실패 발생 시 errorType, retryCount 세팅 : 예외 발생 시 errorType을 적절히 분류하여 저장
5. 일정 횟수 이상 실패 시 운영자에게 알림(이메일, 슬랙 등) 발송
    */
    private final FailedNotificationService failedNotificationService;

    @PatchMapping("/{id}/retry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FailedNotificationResponse>> retry(
            @PathVariable Long id
    ) {
        FailedNotificationResponse response = failedNotificationService.manualRetriedFailedNotification(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(
                        true,
                        HttpStatus.OK,
                        "재처리 성공",
                        response
                ));
    }
}
