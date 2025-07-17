package com.dementor.domain.notification.entity;

import com.dementor.domain.member.entity.Member;
import com.dementor.global.base.BaseEntity;
import com.dementor.global.converter.JsonConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedNotification extends BaseEntity {
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

    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> data;

    @Column(length = 1000)
    private String errorMessage;

    private boolean retried;

    private LocalDateTime retriedAt;

    private int retryCount;

    private ErrorType errorType;

    @Builder
    public FailedNotification(Long id, Member receiver, NotificationType type, String content, Map<String, Object> data, String errorMessage, boolean retried, int retryCount, ErrorType errorType) {
        this.id = id;
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.data = data;
        this.errorMessage = errorMessage;
        this.retried = retried;
        this.retryCount = retryCount;
        this.errorType = errorType;
    }
}
