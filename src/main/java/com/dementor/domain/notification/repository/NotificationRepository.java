package com.dementor.domain.notification.repository;

import com.dementor.domain.member.entity.Member;
import com.dementor.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverAndIsReadFalse(Member receiver);
    Page<Notification> findByReceiverOrderByCreatedAtDesc(Member receiver, Pageable pageable);
    long countByReceiverAndIsReadFalse(Member receiver);

}
