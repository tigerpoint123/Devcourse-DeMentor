package com.dementor.domain.apply.event;

public record MentoringApplyEvent(
        Long applyId,
        Long mentoringClassId,
        Long mentorId,
        Long memberId,
        String className,
        String memberNickname
) {

}
