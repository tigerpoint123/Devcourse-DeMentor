package com.dementor.domain.notification.service;

import com.dementor.domain.notification.dto.request.NotificationRequest;
import com.dementor.domain.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    void receiveNotification(Long memberId, NotificationRequest request) throws Exception;
    List<NotificationResponse> getUnreadNotifications(Long memberId);
    Page<NotificationResponse> getNotifications(Long memberId, Pageable pageable);
    void markAsRead(Long notificationId, Long memberId);
    long getUnreadCount(Long memberId);
} 