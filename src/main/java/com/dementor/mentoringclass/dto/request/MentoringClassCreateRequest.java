package com.dementor.mentoringclass.dto.request;

public record MentoringClassCreateRequest(
        Long mentor_id,
        String stack,
        String content,
        String title,
        int price
) {
}
