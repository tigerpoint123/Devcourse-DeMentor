package com.dementor.domain.mentor.dto.response;

import java.util.List;

public class MentorScheduleResponse {
    // 일정 변경 응답 DTO
    public record ScheduleChangeResponse(
            String yearMonthDay,
            List<Integer> updatedTimePattern,
            Long applymentId
    ) {}

    // 충돌하는 약속 DTO
    public record ConflictingAppointment(
            String date,
            List<Integer> timePattern
    ) {}

    // 충돌 응답 DTO
    public record ScheduleConflictResponse(
            List<ConflictingAppointment> conflictingAppointments
    ) {}
}
