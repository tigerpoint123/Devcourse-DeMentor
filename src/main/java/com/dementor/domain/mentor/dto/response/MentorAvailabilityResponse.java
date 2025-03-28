package com.dementor.domain.mentor.dto.response;

import java.util.List;

public class MentorAvailabilityResponse {
    // 특정 날짜의 가용 시간 정보 DTO
    public record DailyAvailability(
            String date,
            List<String> availableTimeSlots,
            List<String> bookedTimeSlots
    ) {}

    // 가용 시간 조회 응답 DTO
    public record AvailabilityResponse(
            Long memberId,
            String yearMonthDay,
            Integer daysInMonth,
            List<DailyAvailability> availableDates
    ) {}

    // 가용 시간 수정 응답 DTO
    public record AvailabilityUpdateResponse(
            String yearMonthDay,
            List<String> availableTimeSlots
    ) {}
}
