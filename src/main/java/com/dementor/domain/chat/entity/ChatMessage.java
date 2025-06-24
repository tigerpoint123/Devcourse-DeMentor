package com.dementor.domain.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // 발신자 ID (member 또는 admin)
    @Column(nullable = false)
    private Long senderId;

    // 발신자 유형: MEMBER / ADMIN
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType senderType;

    //메시지 본문
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // ✅ ZonedDateTime 적용 엔티티
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private ZonedDateTime sentAt; // ✅ LocalDateTime → ZonedDateTime 변경

    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = ZonedDateTime.now(); // ✅ Asia/Seoul 생략, JVM 기본 시간대 사용
        }
    }

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

}
