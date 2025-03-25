package com.dementor.domain.mentoringclass.dto.request;

public record MentoringClassCreateRequest(
        Long mentor_id,
        String stack,
        String content,
        String title,
        int price
) {
}
