package com.dementor.domain.notification.controller;

import com.dementor.domain.notification.dto.response.NotificationResponse;
import com.dementor.domain.notification.service.NotificationService;
import com.dementor.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        return ResponseEntity.ok(notificationService.getNotifications(memberId, pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        return ResponseEntity.ok(notificationService.getUnreadNotifications(memberId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication
            ) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        return ResponseEntity.ok(notificationService.getUnreadCount(memberId));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
            ) {
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        Long memberId = userDetails.getId();

        notificationService.markAsRead(notificationId, memberId);
        return ResponseEntity.ok().build();
    }
}
