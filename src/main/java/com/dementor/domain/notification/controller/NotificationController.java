package com.dementor.domain.notification.controller;

import com.dementor.domain.notification.dto.response.NotificationResponse;
import com.dementor.domain.notification.service.NotificationService;
import com.dementor.global.ApiResponse;
import com.dementor.global.security.CustomUserDetails;
import com.dementor.global.swaggerDocs.NotificationSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationSwagger {
    // applyService -> notificationEventListener -> notificationQueueListener -> notificationServiceImpl
    /*
    TODO : DLQ로 넘어갈 때 운영자 알림 (이메일, SMS, 슬랙 등) 전송 로직 필요
✅ 고려할 수 있는 고도화 포인트
1. ✅ 재처리 기능 (이미 설명한 것 포함)
운영자가 특정 메시지를 재처리할 수 있는 수동 재처리 API or 버튼
예외 패턴에 따라 자동 재처리 룰 구성 (ex. 특정 에러만 자동 복구)

2. ✅ 실패 유형 분석 및 지표화
실패 원인 분류 (ex. 타임아웃, 포맷 오류, 대상 없음 등)
실패율 / 성공률 / DLQ 적재율 등을 Prometheus + Grafana로 시각화

3. ✅ 메시지 추적 (Traceability)
요청 ID, 사용자 ID를 메시지에 포함 → 로그 상에서 요청 흐름 추적
Correlation ID 패턴 사용

4. ✅ 보안 & 유효성
메시지 페이로드 검증 (예: 필수 필드 누락 시 Drop)
인증된 서비스만 Queue에 발행 가능하도록 ACL 설정

5. ✅ 멀티 채널 대응 (선택)
예: 알림 발송 실패 시 이메일 → 푸시 → SMS 순차 fallback 전략
    * */

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            Authentication authentication,
            Pageable pageable
    ) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        Page<NotificationResponse> responses = notificationService.getNotifications(memberId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.OK,
                                "알림 조회 성공",
                                responses
                        )
                );
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        List<NotificationResponse> responses = notificationService.getUnreadNotifications(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.OK,
                                "읽지 않은 알림 조회 성공",
                                responses
                        )
                );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            Authentication authentication
            ) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        Long unreadCount = notificationService.getUnreadCount(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.OK,
                                "안 읽은 알림 개수 조회 성공",
                                unreadCount
                        )
                );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
            ) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        notificationService.markAsRead(notificationId, memberId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        ApiResponse.of(
                                true,
                                HttpStatus.NO_CONTENT,
                                "읽기 처리 성공"
                        )
                );
    }
}
