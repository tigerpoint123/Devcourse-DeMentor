package com.dementor.domain.notification.service;

import com.dementor.domain.notification.dto.response.FailedNotificationResponse;

public interface FailedNotificationService {

    FailedNotificationResponse manualRetriedFailedNotification(Long id);
    void autoRetriedFailedNotification();
}
