package com.dementor.domain.mentor.dto.request;

import jakarta.validation.constraints.Min;

import java.util.List;

public class MentorChangeRequest {
    // 요청 파라미터를 위한 DTO
    public record ModificationRequestParams(
            String status,
            @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
            Integer page,

            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            Integer size
    ) {
        // 기본값을 설정하는 정적 팩토리 메서드
        public static ModificationRequestParams defaultParams() {
            return new ModificationRequestParams(null, 1, 10);
        }

        // 유효한 상태값인지 검증하는 메서드
        public boolean hasValidStatus() {
            return status == null ||
                    List.of("PENDING", "APPROVED", "REJECTED").contains(status);
        }
    }
}
