package com.dementor.domain.mentor.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MentorModificationRequestsResponse {
    // 수정 필드 값 DTO (변경 전/후)
    public record ModifiedFieldValue<T>(
            T before,
            T after
    ) {}

    // 수정 요청 개별 항목 DTO
    public record ModificationRequestItem(
            Long requestId,
            String status,
            LocalDateTime requestDate,
            Map<String, ModifiedFieldValue<?>> modifiedFields
    ) {}

    // 페이지네이션 정보 DTO
    public record Pagination(
            Integer page,
            Integer size,
            Long totalElements
    ) {}

    // 응답 데이터 DTO
    public record ModificationRequestsResponse(
            List<ModificationRequestItem> modificationRequests,
            Pagination pagination
    ) {}
}
