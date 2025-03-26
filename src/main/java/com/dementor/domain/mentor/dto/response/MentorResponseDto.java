package com.dementor.domain.mentor.dto.response;

import com.dementor.domain.mentor.entity.MentorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MentorResponseDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Basic {
        private Long mentorId;
        private Long jobId;
        private Long userId;
        private String name;
        private Integer career;
        private String phone;
        private String introduction;
        private Boolean isApproved;
        private Boolean isModified;

        // Entity를 DTO로 변환하는 정적 메서드
        public static Basic from(MentorEntity entity) {
            return Basic.builder()
                    .mentorId(entity.getMentorId())
                    .jobId(entity.getJobId())
                    .userId(entity.getUserId())
                    .name(entity.getName())
                    .career(entity.getCareer())
                    .phone(entity.getPhone())
                    .introduction(entity.getIntroduction())
                    .isApproved(entity.getIsApproved())
                    .isModified(entity.getIsModified())
                    .build();
        }
    }
}
