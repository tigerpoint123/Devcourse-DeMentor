package com.dementor.domain.mentoringclass.dto.response;

public record MentoringClassSelectResponse(
        Long id,
        String stack,
        String content,
        String title,
        int price,
        Long mentor_id
) {
}
