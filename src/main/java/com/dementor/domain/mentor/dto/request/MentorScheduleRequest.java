package com.dementor.domain.mentor.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MentorScheduleRequest(
        @NotNull(message = "멘토링 클래스 ID는 필수입니다.")
        Long classId,

        @NotNull(message = "원래 일정 날짜는 필수입니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 YYYY-MM-DD이어야 합니다.")
        String yearMonthDay,

        @NotNull(message = "원래 시간 패턴은 필수입니다.")
        @Size(min = 24, max = 24, message = "시간 패턴은 24개의 요소를 가져야 합니다.")
        List<Integer> timePattern,

        @NotNull(message = "멘토링 신청 ID는 필수입니다.")
        Long applymentId,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 YYYY-MM-DD이어야 합니다.")
        String newYearMonthDay,

        @NotNull(message = "새 시간 패턴은 필수입니다.")
        @Size(min = 24, max = 24, message = "시간 패턴은 24개의 요소를 가져야 합니다.")
        List<Integer> newTimePattern
) {}
