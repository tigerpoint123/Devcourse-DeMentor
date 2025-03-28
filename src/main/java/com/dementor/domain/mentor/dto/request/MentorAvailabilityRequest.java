package com.dementor.domain.mentor.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class MentorAvailabilityRequest {
    // 가용 시간 조회 요청 파라미터 DTO
    public record AvailabilityQueryParams(
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 YYYY-MM-DD이어야 합니다.")
            String yearMonthDay,

            Long classId
    ) {}

    // 가용 시간 수정 요청 DTO
    public record AvailabilityUpdateRequest(
            @NotNull(message = "멘토링 클래스 ID는 필수입니다.")
            Long classId,

            @NotNull(message = "시간 패턴은 필수입니다.")
            @Size(min = 24, max = 24, message = "시간 패턴은 24개의 요소를 가져야 합니다.")
            List<Integer> timePattern
    ) {}
}
