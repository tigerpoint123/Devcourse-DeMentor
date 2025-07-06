package com.dementor.domain.notification.repository;

import com.dementor.domain.notification.entity.FailedNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedNotificationRepository extends JpaRepository<FailedNotification, Long> {
}
