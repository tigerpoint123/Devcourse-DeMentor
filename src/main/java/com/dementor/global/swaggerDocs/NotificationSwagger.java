package com.dementor.global.swaggerDocs;

import com.dementor.domain.notification.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "알림", description = "비동기 알림 기능")
public interface NotificationSwagger {
    @Operation(summary = "알림 전체 조회", description = "사용자의 알림을 전부 조회합니다.")
    ResponseEntity<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @Operation(summary = "미확인 알림 조회", description = "사용자가 읽지 않은 알림을 조회합니다.")
    ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            Authentication authentication
    );

    @Operation(summary = "미확인 알림 개수 조회", description = "사용자가 읽지 않은 알림의 개수를 조회합니다.")
    ResponseEntity<Long> getUnreadCount(
            Authentication authentication
    );

    @Operation(summary = "알림 확인", description = "특정 알림을 확인하고 is_read 상태를 true로 바꿉니다.")
    ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    );
}
