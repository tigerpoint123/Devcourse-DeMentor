package com.dementor.domain.mentoringclass.dto.response;

public record MentorInfo(
        Long mentorId,
        String name,
        Integer career,
        String introduction
) {
}
