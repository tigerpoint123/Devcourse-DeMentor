package com.dementor.domain.notification.service;

import com.dementor.domain.member.entity.Member;
import com.dementor.domain.member.exception.MemberErrorCode;
import com.dementor.domain.member.exception.MemberException;
import com.dementor.domain.member.repository.MemberRepository;
import com.dementor.domain.notification.dto.request.NotificationRequest;
import com.dementor.domain.notification.dto.response.NotificationResponse;
import com.dementor.domain.notification.entity.Notification;
import com.dementor.domain.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void sendNotification(Long memberId, NotificationRequest request) throws Exception {
        Member receiver = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        try {
            Notification notification = Notification.builder()
                    .receiver(receiver)
                    .type(request.type())
                    .content(request.content())
                    .data(request.data())
                    .build();

            notificationRepository.save(notification);

            // WebSocket으로 실시간 알림 전송
            NotificationResponse response = NotificationResponse.from(notification);
            messagingTemplate.convertAndSendToUser(
                    receiver.getId().toString(),
                    "/queue/notifications",
                    response
            );

        } catch (Exception e) {
            log.error("Failed to send notification to user: {}", receiver.getId(), e);
            throw new Exception("알림 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public List<NotificationResponse> getUnreadNotifications(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return notificationRepository.findByReceiverAndIsReadFalse(member)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    public Page<NotificationResponse> getNotifications(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        return notificationRepository.findByReceiverOrderByCreatedAtDesc(member, pageable)
                .map(NotificationResponse::from);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().equals(member)) {
            throw new AccessDeniedException("해당 알림에 대한 권한이 없습니다.");
        }

        notification.markAsRead();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        return notificationRepository.countByReceiverAndIsReadFalse(member);
    }
}
