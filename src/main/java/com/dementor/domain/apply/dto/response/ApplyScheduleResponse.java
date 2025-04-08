package com.dementor.domain.apply.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.dementor.domain.apply.entity.Apply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplyScheduleResponse {
    private List<ScheduleItem> applyments;
    private Pagination pagination;

    public static ApplyScheduleResponse from(Page<Apply> applyPage, int page, int size) {
        List<ScheduleItem> scheduleItems = applyPage.getContent().stream()
                .map(apply -> ScheduleItem.builder()
                        .classId(apply.getMentoringClass().getId())
                        .schedule(apply.getSchedule())
                        .build())
                .collect(Collectors.toList());

        Pagination pagination = Pagination.builder()
                .page(page)
                .size(size)
                .totalElements(applyPage.getTotalElements())
                .totalPages(applyPage.getTotalPages())
                .build();

        return ApplyScheduleResponse.builder()
                .applyments(scheduleItems)
                .pagination(pagination)
                .build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScheduleItem {
        private Long classId;
        private LocalDateTime schedule;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pagination {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
} 