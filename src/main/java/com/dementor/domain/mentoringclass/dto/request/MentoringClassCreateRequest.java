package com.dementor.domain.mentoringclass.dto.request;

public record MentoringClassCreateRequest(
        String stack,
        String content,
        String title,
        int price
) {
}
