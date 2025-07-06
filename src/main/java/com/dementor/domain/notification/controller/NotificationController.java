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
    // TODO : DLQ 처리 로직 추가 필요

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
