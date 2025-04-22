package com.dementor.domain.notification.entity;

import com.dementor.domain.member.entity.Member;
import com.dementor.global.base.BaseEntity;
import com.dementor.global.converter.JsonConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification")
public class Notification extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member receiver;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String content;

    private boolean isRead;

    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> data;

    @Builder
    public Notification(Member receiver, NotificationType type, String content, Map<String, Object> data) {
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.data = data;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
