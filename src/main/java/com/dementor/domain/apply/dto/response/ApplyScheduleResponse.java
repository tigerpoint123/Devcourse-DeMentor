package com.dementor.domain.apply.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.dementor.domain.apply.entity.Apply;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyScheduleResponse {
    private List<ScheduleItem> applyments;
    private Pagination pagination;

    public static ApplyScheduleResponse from(Page<Apply> applyPage, int page, int size) {
        List<ScheduleItem> scheduleItems = applyPage.getContent().stream()
                .map(apply -> ScheduleItem.builder()
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
    @Builder
    public static class ScheduleItem {
        private LocalDateTime schedule;
    }

    @Getter
    @Builder
    public static class Pagination {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
} 